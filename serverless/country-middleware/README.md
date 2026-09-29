# 🌍 Countries Middleware (Hono + TypeScript Cloudflare Edge Worker)

A high-performance, edge-caching API gateway for **Countries Compose**. Built with **Hono** and **TypeScript**, deployed on **Cloudflare Workers**.

---

## ⚡ Highlights

* **100% Free & Fast**: Replaces Netlify's 300-credit hard limit with Cloudflare's **100,000 free requests/day** and zero egress bandwidth fees.
* **Hono Framework**: Modern web standards (`Request`, `Response`), lightweight (<15KB), sub-millisecond route matching, and built-in CORS.
* **Upstash Redis Caching**: 4-hour distributed cache for country dossiers.
* **Upstream Protection**: Atomic Lua rate-limiting script (`ADMISSION_SCRIPT`) capping upstream calls to RestCountries to 15 req / 10s ceiling.
* **In-Flight Deduplication**: Prevents cache stampedes when multiple clients request the same country simultaneously.
* **Edge CDN Caching**: Automatically sets `Cache-Control: public, max-age=900, s-maxage=900` for 15-minute global edge delivery.

---

## 🚀 Getting Started

### Local Development
```bash
npm run dev
```

### Type Checking & Build
```bash
npx tsc --noEmit
```

### Run Tests
From the repository root:
```bash
npm test
```

### Deploy to Cloudflare
```bash
npm run deploy
```

---

## 🔐 Secrets Configuration

Set the required environment variables in Cloudflare:
```bash
npx wrangler secret put REST_COUNTRIES_API_KEY
npx wrangler secret put UPSTASH_REDIS_REST_URL
npx wrangler secret put UPSTASH_REDIS_REST_TOKEN
```

---

## 📡 Endpoints

* `GET /health` or `GET /api/health` - Readiness probe checking API key and Upstash configuration.
* `GET /api/countries/:code` (or `/:code`) - Returns the country dossier for the given ISO 3166-1 alpha-2 or alpha-3 code.
