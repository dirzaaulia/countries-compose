import assert from "node:assert/strict";
import { test } from "node:test";
import { createCountryApp } from "../serverless/country-middleware/src/index.ts";

function fakeRedis() {
  const entries = new Map();
  return {
    async get(key) { return entries.get(key); },
    async set(key, value) { entries.set(key, value); },
    async eval() { return 1; },
    entries
  };
}

test("validates ISO codes before reaching storage", async () => {
  const app = createCountryApp({ redis: fakeRedis(), apiKey: "test" });
  const response = await app.request("https://example.net/api/countries/INVALID_CODE");
  assert.equal(response.status, 400);
});

test("ignores platform query parameters while validating the country code", async () => {
  const app = createCountryApp({
    redis: fakeRedis(),
    apiKey: "test",
    fetcher: async () => Response.json({ data: { objects: [{ names: { common: "Canada" } }] } })
  });
  const response = await app.request("https://example.net/api/countries/CA?site=cloudflare");
  assert.equal(response.status, 200);
});

test("shares upstream result across repeated requests", async () => {
  const redis = fakeRedis();
  let calls = 0;
  const app = createCountryApp({
    redis,
    apiKey: "test",
    fetcher: async () => {
      calls++;
      return Response.json({ data: { objects: [{ names: { common: "Canada" } }] } });
    }
  });
  const url = "https://example.net/api/countries/CA";
  assert.equal((await app.request(url)).status, 200);
  assert.equal((await app.request(url)).status, 200);
  assert.equal(calls, 1);
  assert.equal(redis.entries.size, 1);
});

test("does not cache upstream failures", async () => {
  const redis = fakeRedis();
  const app = createCountryApp({ redis, apiKey: "test", fetcher: async () => new Response("", { status: 429 }) });
  assert.equal((await app.request("https://example.net/api/countries/CA")).status, 503);
  assert.equal(redis.entries.size, 0);
});

test("reports failing Redis operation without exposing the error to clients", async () => {
  const failures = [];
  const redis = { ...fakeRedis(), async eval() { throw new Error("private Redis detail"); } };
  const app = createCountryApp({ redis, apiKey: "test", logError: (...args) => failures.push(args) });
  const response = await app.request("https://example.net/api/countries/CA");
  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), { error: "Country service unavailable" });
  assert.deepEqual(failures, [["Country middleware failure", "upstream admission", "Error", null]]);
});

test("classifies Upstash admission failures without logging command arguments", async () => {
  const failures = [];
  const redis = {
    ...fakeRedis(),
    async eval() {
      const error = new Error('ERR unknown command `EVAL`, command was: ["EVAL","secret"]');
      error.name = "UpstashError";
      throw error;
    }
  };
  const app = createCountryApp({ redis, apiKey: "test", logError: (...args) => failures.push(args) });
  const response = await app.request("https://example.net/api/countries/CA");
  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), { error: "Country service unavailable" });
  assert.deepEqual(failures, [["Country middleware failure", "upstream admission", "UpstashError", "unsupported command"]]);
  assert.doesNotMatch(JSON.stringify(failures), /secret/);
});

test("reports failing upstream operation without exposing the error to clients", async () => {
  const failures = [];
  const app = createCountryApp({
    redis: fakeRedis(),
    apiKey: "test",
    fetcher: async () => { throw new Error("private upstream detail"); },
    logError: (...args) => failures.push(args)
  });
  const response = await app.request("https://example.net/api/countries/CA");
  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), { error: "Country service unavailable" });
  assert.deepEqual(failures, [["Country middleware failure", "upstream request", "Error", null]]);
});
