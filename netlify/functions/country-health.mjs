export default async function health() {
  return Response.json({ ready: Boolean(
    process.env.REST_COUNTRIES_API_KEY &&
    process.env.UPSTASH_REDIS_REST_URL &&
    process.env.UPSTASH_REDIS_REST_TOKEN
  ) }, { headers: { "Cache-Control": "no-store" } });
}
