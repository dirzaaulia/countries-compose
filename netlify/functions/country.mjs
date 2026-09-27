import { Redis } from "@upstash/redis";

const CACHE_SECONDS = 4 * 60 * 60;
const EDGE_SECONDS = 15 * 60;
const UPSTREAM_WINDOW = 10;
const UPSTREAM_LIMIT = 15; // Leave headroom below the provider's 20 / 10s ceiling.
const ADMISSION_SCRIPT = `
local count = redis.call('INCR', KEYS[1])
if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
if count > tonumber(ARGV[2]) then return 0 end
return 1
`;
const inflight = new Map();

function admissionErrorReason(error) {
  if (error?.name !== "UpstashError") return null;
  const reason = error.message?.split(", command was:", 1)[0] ?? "";
  if (/unknown command|unsupported command/i.test(reason)) return "unsupported command";
  if (/permission|not allowed|disabled|read.?only|noauth|unauthorized|forbidden/i.test(reason)) return "permission denied";
  if (/lua|script|syntax|wrong number of arguments/i.test(reason)) return "script rejected";
  if (/rate limit|quota|limit exceeded|too many requests/i.test(reason)) return "Upstash limit";
  return "unclassified Upstash response";
}

function jsonResponse(body, status, cache = false) {
  return new Response(JSON.stringify(body), {
    status,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      "Cache-Control": "no-store",
      "Access-Control-Allow-Origin": "*",
      ...(cache ? { "Netlify-CDN-Cache-Control": `public, durable, max-age=${EDGE_SECONDS}` } : {})
    }
  });
}

export function createHandler({ redis, apiKey, fetcher = fetch, clock = Date.now, logError = console.error }) {
  return async function handle(request, context = {}) {
    if (request.method !== "GET") return jsonResponse({ error: "Method not allowed" }, 405);
    const code = context.params?.code?.trim().toUpperCase();
    if (!code || !/^[A-Z]{2,3}$/.test(code)) return jsonResponse({ error: "Invalid ISO code" }, 400);
    if (!apiKey || !redis) return jsonResponse({ error: "Country service unavailable" }, 503);

    const key = `country:v5:full:${code}`;
    let operation = "cache read";
    try {
      const cached = await redis.get(key);
      if (cached && cached.expiresAt > clock()) return jsonResponse(cached.body, 200, true);

      let pending = inflight.get(key);
      if (!pending) {
        pending = (async () => {
          // Redis EVAL atomically caps total upstream traffic across function instances.
          operation = "upstream admission";
          const admitted = await redis.eval(
            ADMISSION_SCRIPT,
            [`country:upstream:${Math.floor(clock() / (UPSTREAM_WINDOW * 1000))}`],
            [UPSTREAM_WINDOW + 1, UPSTREAM_LIMIT]
          );
          if (Number(admitted) !== 1) return jsonResponse({ error: "Country service busy" }, 429);

          const field = code.length === 2 ? "codes.alpha_2" : "codes.alpha_3";
          operation = "upstream request";
          const upstream = await fetcher(`https://api.restcountries.com/countries/v5/${field}/${code}`, {
            headers: { Authorization: `Bearer ${apiKey}` },
            signal: AbortSignal.timeout(8000)
          });
          if (upstream.status === 429) return jsonResponse({ error: "Country service busy" }, 503);
          if (upstream.status === 404) return jsonResponse({ error: "Country not found" }, 404);
          if (!upstream.ok) return jsonResponse({ error: "Country service unavailable" }, 502);
          operation = "upstream JSON";
          const envelope = await upstream.json();
          const country = envelope?.data?.objects?.[0];
          if (!country) return jsonResponse({ error: "Country not found" }, 404);
          const body = { data: { objects: [country] } };
          operation = "cache write";
          await redis.set(key, { body, expiresAt: clock() + CACHE_SECONDS * 1000 }, { ex: CACHE_SECONDS });
          return jsonResponse(body, 200, true);
        })().finally(() => inflight.delete(key));
        inflight.set(key, pending);
      }
      return await pending;
    } catch (error) {
      logError(
        "Country middleware failure",
        operation,
        error instanceof Error ? error.name : "UnknownError",
        operation === "upstream admission" ? admissionErrorReason(error) : null
      );
      return jsonResponse({ error: "Country service unavailable" }, 503);
    }
  };
}

let handler;
export default function country(request, context) {
  if (!handler) {
    const url = process.env.UPSTASH_REDIS_REST_URL;
    const token = process.env.UPSTASH_REDIS_REST_TOKEN;
    handler = createHandler({
      redis: url && token ? new Redis({ url, token }) : null,
      apiKey: process.env.REST_COUNTRIES_API_KEY
    });
  }
  return handler(request, context);
}

export const config = { path: "/api/countries/:code" };
