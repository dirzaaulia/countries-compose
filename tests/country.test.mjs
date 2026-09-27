import assert from "node:assert/strict";
import { test } from "node:test";
import { createHandler } from "../netlify/functions/country.mjs";

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
  const handler = createHandler({ redis: fakeRedis(), apiKey: "test" });
  const response = await handler(new Request("https://example.net/api/countries/CA"), { params: { code: "../../../etc" } });
  assert.equal(response.status, 400);
});

test("ignores platform query parameters while validating the country code", async () => {
  const handler = createHandler({
    redis: fakeRedis(),
    apiKey: "test",
    fetcher: async () => Response.json({ data: { objects: [{ names: { common: "Canada" } }] } })
  });
  const response = await handler(new Request("https://example.net/api/countries/CA?site=netlify"), { params: { code: "CA" } });
  assert.equal(response.status, 200);
});

test("shares upstream result across repeated requests", async () => {
  const redis = fakeRedis();
  let calls = 0;
  const handler = createHandler({ redis, apiKey: "test", fetcher: async () => {
    calls++;
    return Response.json({ data: { objects: [{ names: { common: "Canada" } }] } });
  } });
  const url = "https://example.net/api/countries/CA";
  const context = { params: { code: "CA" } };
  assert.equal((await handler(new Request(url), context)).status, 200);
  assert.equal((await handler(new Request(url), context)).status, 200);
  assert.equal(calls, 1);
  assert.equal(redis.entries.size, 1);
});

test("does not cache upstream failures", async () => {
  const redis = fakeRedis();
  const handler = createHandler({ redis, apiKey: "test", fetcher: async () => new Response("", { status: 429 }) });
  assert.equal((await handler(new Request("https://example.net/api/countries/CA"), { params: { code: "CA" } })).status, 503);
  assert.equal(redis.entries.size, 0);
});

test("reports failing Redis operation without exposing the error to clients", async () => {
  const failures = [];
  const redis = { ...fakeRedis(), async eval() { throw new Error("private Redis detail"); } };
  const handler = createHandler({ redis, apiKey: "test", logError: (...args) => failures.push(args) });
  const response = await handler(new Request("https://example.net/api/countries/CA"), { params: { code: "CA" } });
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
  const handler = createHandler({ redis, apiKey: "test", logError: (...args) => failures.push(args) });
  const response = await handler(new Request("https://example.net/api/countries/CA"), { params: { code: "CA" } });
  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), { error: "Country service unavailable" });
  assert.deepEqual(failures, [["Country middleware failure", "upstream admission", "UpstashError", "unsupported command"]]);
  assert.doesNotMatch(JSON.stringify(failures), /secret/);
});

test("reports failing upstream operation without exposing the error to clients", async () => {
  const failures = [];
  const handler = createHandler({
    redis: fakeRedis(), apiKey: "test",
    fetcher: async () => { throw new Error("private upstream detail"); },
    logError: (...args) => failures.push(args)
  });
  const response = await handler(new Request("https://example.net/api/countries/CA"), { params: { code: "CA" } });
  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), { error: "Country service unavailable" });
  assert.deepEqual(failures, [["Country middleware failure", "upstream request", "Error", null]]);
});
