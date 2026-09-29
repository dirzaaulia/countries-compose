import { Hono } from "hono";
import { cors } from "hono/cors";
import { Redis } from "@upstash/redis/cloudflare";

export type Bindings = {
  REST_COUNTRIES_API_KEY?: string;
  UPSTASH_REDIS_REST_URL?: string;
  UPSTASH_REDIS_REST_TOKEN?: string;
};

export interface HandlerOptions {
  redis?: {
    get: (key: string) => Promise<any>;
    set: (key: string, value: any, opts?: any) => Promise<any>;
    eval: (script: string, keys: string[], args: any[]) => Promise<any>;
  } | null;
  apiKey?: string | null;
  fetcher?: typeof fetch;
  clock?: () => number;
  logError?: (...args: any[]) => void;
}

const CACHE_SECONDS = 4 * 60 * 60; // 4 hours in Redis
const EDGE_SECONDS = 15 * 60;      // 15 minutes at Edge CDN
const UPSTREAM_WINDOW = 10;
const UPSTREAM_LIMIT = 15;        // Below upstream 20 / 10s ceiling

const ADMISSION_SCRIPT = `
local count = redis.call('INCR', KEYS[1])
if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
if count > tonumber(ARGV[2]) then return 0 end
return 1
`;

function admissionErrorReason(error: any): string | null {
  if (error?.name !== "UpstashError") return null;
  const reason = error.message?.split(", command was:", 1)[0] ?? "";
  if (/unknown command|unsupported command/i.test(reason)) return "unsupported command";
  if (/permission|not allowed|disabled|read.?only|noauth|unauthorized|forbidden/i.test(reason)) return "permission denied";
  if (/lua|script|syntax|wrong number of arguments/i.test(reason)) return "script rejected";
  if (/rate limit|quota|limit exceeded|too many requests/i.test(reason)) return "Upstash limit";
  return "unclassified Upstash response";
}

export function createCountryApp(options: HandlerOptions = {}) {
  const app = new Hono<{ Bindings: Bindings }>();
  const inflight = new Map<string, Promise<Response>>();

  const clock = options.clock ?? Date.now;
  const logError = options.logError ?? console.error;
  const fetcher = options.fetcher ?? fetch;

  // Global CORS middleware
  app.use("*", cors());

  // Health check endpoint
  const handleHealth = (c: any) => {
    const apiKey = options.apiKey ?? c.env?.REST_COUNTRIES_API_KEY;
    const redisUrl = c.env?.UPSTASH_REDIS_REST_URL;
    const redisToken = c.env?.UPSTASH_REDIS_REST_TOKEN;
    const hasRedis = Boolean(options.redis || (redisUrl && redisToken));

    return c.json(
      { ready: Boolean(apiKey && hasRedis) },
      200,
      { "Cache-Control": "no-store" }
    );
  };

  app.get("/health", handleHealth);
  app.get("/api/health", handleHealth);

  // Country handler
  const handleCountry = async (c: any) => {
    const rawCode = c.req.param("code")?.trim().toUpperCase();
    if (!rawCode || !/^[A-Z]{2,3}$/.test(rawCode)) {
      return c.json({ error: "Invalid ISO code" }, 400);
    }
    const code = rawCode;

    const apiKey = options.apiKey ?? c.env?.REST_COUNTRIES_API_KEY;
    let redis = options.redis;
    if (redis === undefined) {
      const url = c.env?.UPSTASH_REDIS_REST_URL;
      const token = c.env?.UPSTASH_REDIS_REST_TOKEN;
      redis = url && token ? new Redis({ url, token }) : null;
    }

    if (!apiKey || !redis) {
      return c.json({ error: "Country service unavailable" }, 503);
    }

    const key = `country:v5:full:${code}`;
    let operation = "cache read";

    try {
      // 1. Check Redis cache
      const cached: any = await redis.get(key);
      if (cached && cached.expiresAt > clock()) {
        return c.json(cached.body, 200, {
          "Cache-Control": `public, max-age=${EDGE_SECONDS}, s-maxage=${EDGE_SECONDS}`,
        });
      }

      // 2. In-flight request deduplication
      let pending = inflight.get(key);
      if (!pending) {
        pending = (async () => {
          // Redis EVAL atomically caps total upstream traffic across instances
          operation = "upstream admission";
          const admitted = await redis!.eval(
            ADMISSION_SCRIPT,
            [`country:upstream:${Math.floor(clock() / (UPSTREAM_WINDOW * 1000))}`],
            [UPSTREAM_WINDOW + 1, UPSTREAM_LIMIT]
          );

          if (Number(admitted) !== 1) {
            return c.json({ error: "Country service busy" }, 429);
          }

          const field = code.length === 2 ? "codes.alpha_2" : "codes.alpha_3";
          operation = "upstream request";
          const upstream = await fetcher(`https://api.restcountries.com/countries/v5/${field}/${code}`, {
            headers: { Authorization: `Bearer ${apiKey}` },
            signal: AbortSignal.timeout(8000),
          });

          if (upstream.status === 429) return c.json({ error: "Country service busy" }, 503);
          if (upstream.status === 404) return c.json({ error: "Country not found" }, 404);
          if (!upstream.ok) return c.json({ error: "Country service unavailable" }, 502);

          operation = "upstream JSON";
          const envelope: any = await upstream.json();
          const country = envelope?.data?.objects?.[0];
          if (!country) return c.json({ error: "Country not found" }, 404);

          const body = { data: { objects: [country] } };

          operation = "cache write";
          await redis!.set(key, { body, expiresAt: clock() + CACHE_SECONDS * 1000 }, { ex: CACHE_SECONDS });

          return c.json(body, 200, {
            "Cache-Control": `public, max-age=${EDGE_SECONDS}, s-maxage=${EDGE_SECONDS}`,
          });
        })().finally(() => inflight.delete(key));

        inflight.set(key, pending);
      }

      return await pending;
    } catch (error: any) {
      logError(
        "Country middleware failure",
        operation,
        error instanceof Error ? error.name : "UnknownError",
        operation === "upstream admission" ? admissionErrorReason(error) : null
      );
      return c.json({ error: "Country service unavailable" }, 503);
    }
  };

  app.get("/api/countries/:code", handleCountry);
  app.get("/:code", handleCountry);

  return app;
}

// Default export for Cloudflare Workers
const app = createCountryApp();
export default app;
