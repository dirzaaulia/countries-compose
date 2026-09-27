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
  const response = await handler(new Request("https://example.net/api/countries/xx?code=../../../etc"));
  assert.equal(response.status, 400);
});

test("shares upstream result across repeated requests", async () => {
  const redis = fakeRedis();
  let calls = 0;
  const handler = createHandler({ redis, apiKey: "test", fetcher: async () => {
    calls++;
    return Response.json({ data: { objects: [{ names: { common: "Canada" } }] } });
  } });
  const url = "https://example.net/api/countries/CA?code=CA";
  assert.equal((await handler(new Request(url))).status, 200);
  assert.equal((await handler(new Request(url))).status, 200);
  assert.equal(calls, 1);
  assert.equal(redis.entries.size, 1);
});

test("does not cache upstream failures", async () => {
  const redis = fakeRedis();
  const handler = createHandler({ redis, apiKey: "test", fetcher: async () => new Response("", { status: 429 }) });
  assert.equal((await handler(new Request("https://example.net/api/countries/CA?code=CA"))).status, 503);
  assert.equal(redis.entries.size, 0);
});
