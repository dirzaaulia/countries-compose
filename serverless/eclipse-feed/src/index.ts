interface Env {
  ECLIPSE_FEED: KVNamespace;
}

type EclipseKind = "solar" | "lunar";

interface EclipseEvent {
  id: string;
  kind: EclipseKind;
  type: string;
  date: string;
  greatestTimeTd: string;
  magnitude: number;
  saros: number;
  duration: string | null;
  visibility: string;
  mapUrl: string;
  pathUrl: string | null;
  sourceUrl: string;
}

interface EclipseFeed {
  schemaVersion: 1;
  generatedAt: string;
  attribution: string;
  events: EclipseEvent[];
}

const NASA_BASE = "https://eclipse.gsfc.nasa.gov/";
const FEED_KEY = "eclipse-feed:v1";
const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
const headers = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
  "Content-Type": "application/json; charset=UTF-8",
  "Cache-Control": "public, max-age=86400"
};

function stripHtml(value: string): string {
  return value
    .replace(/<br\s*\/?>/gi, " ")
    .replace(/<[^>]+>/g, " ")
    .replace(/&nbsp;/gi, " ")
    .replace(/&amp;/gi, "&")
    .replace(/\s+/g, " ")
    .trim();
}

function firstHref(value: string, pageUrl: string): string | null {
  const match = value.match(/href\s*=\s*["']?([^"'\s>]+)/i);
  return match === null ? null : new URL(match[1], pageUrl).toString();
}

function parseDate(value: string): string | null {
  const match = value.match(/(\d{4})\s+([A-Za-z]{3})\s+(\d{1,2})/);
  if (match === null) return null;

  const month = MONTHS.indexOf(match[2]);
  if (month < 0) return null;
  return `${match[1]}-${String(month + 1).padStart(2, "0")}-${match[3].padStart(2, "0")}`;
}

function decadeStart(year: number): number {
  return Math.floor((year - 1) / 10) * 10 + 1;
}

function parseEvents(html: string, kind: EclipseKind, sourceUrl: string): EclipseEvent[] {
  const rows = html.match(/<tr(?:\s[^>]*)?>([\s\S]*?)<\/tr>/gi) ?? [];
  const events: EclipseEvent[] = [];

  for (const row of rows) {
    const cells = row.match(/<td(?:\s[^>]*)?>([\s\S]*?)<\/td>/gi) ?? [];
    if (cells.length !== 7) continue;

    const date = parseDate(stripHtml(cells[0]));
    if (date === null) continue;

    const type = stripHtml(cells[2]);
    const magnitude = Number.parseFloat(stripHtml(cells[4]));
    const saros = Number.parseInt(stripHtml(cells[3]), 10);
    if (!Number.isFinite(magnitude) || !Number.isFinite(saros)) continue;

    events.push({
      id: `${kind}-${date}`,
      kind,
      type,
      date,
      greatestTimeTd: `${date} ${stripHtml(cells[1])} TD`,
      magnitude,
      saros,
      duration: stripHtml(cells[5]).replace("-", "").trim() || null,
      visibility: stripHtml(cells[6]),
      mapUrl: firstHref(cells[0], sourceUrl) ?? sourceUrl,
      pathUrl: kind === "solar" ? firstHref(cells[5], sourceUrl) : null,
      sourceUrl
    });
  }

  return events;
}

async function refreshFeed(env: Env): Promise<EclipseFeed> {
  const now = new Date();
  const end = new Date(Date.UTC(now.getUTCFullYear() + 3, now.getUTCMonth(), now.getUTCDate()));
  const decades = [...new Set([decadeStart(now.getUTCFullYear()), decadeStart(end.getUTCFullYear())])];
  const sourceUrls = decades.flatMap((start) => [
    { kind: "solar" as const, url: `${NASA_BASE}SEdecade/SEdecade${start}.html` },
    { kind: "lunar" as const, url: `${NASA_BASE}LEdecade/LEdecade${start}.html` }
  ]);

  const pages = await Promise.all(sourceUrls.map(async (source) => {
    const response = await fetch(source.url, { headers: { "User-Agent": "CountriesComposeEclipseFeed/1.0" } });
    if (!response.ok) throw new Error(`NASA source failed: ${response.status}`);
    return { ...source, html: await response.text() };
  }));

  const startDate = `${now.getUTCFullYear()}-01-01`;
  const endDate = end.toISOString().slice(0, 10);
  const events = pages
    .flatMap((page) => parseEvents(page.html, page.kind, page.url))
    .filter((event) => event.date >= startDate && event.date <= endDate)
    .sort((left, right) => left.date.localeCompare(right.date));

  if (events.length === 0) throw new Error("NASA sources returned no upcoming eclipse events");

  const feed: EclipseFeed = {
    schemaVersion: 1,
    generatedAt: now.toISOString(),
    attribution: "Eclipse Predictions by Fred Espenak, NASA's GSFC",
    events
  };
  await env.ECLIPSE_FEED.put(FEED_KEY, JSON.stringify(feed));
  return feed;
}

async function getFeed(env: Env): Promise<EclipseFeed> {
  const cached = await env.ECLIPSE_FEED.get<EclipseFeed>(FEED_KEY, "json");
  return cached ?? refreshFeed(env);
}

export default {
  async fetch(request, env): Promise<Response> {
    if (request.method === "OPTIONS") return new Response(null, { headers });

    const url = new URL(request.url);
    if (request.method !== "GET" || url.pathname !== "/v1/eclipses") {
      return new Response(JSON.stringify({ error: "Not found" }), { status: 404, headers });
    }

    try {
      return new Response(JSON.stringify(await getFeed(env)), { headers });
    } catch (error) {
      return new Response(
        JSON.stringify({ error: "Eclipse feed unavailable", detail: error instanceof Error ? error.message : "Unknown error" }),
        { status: 503, headers }
      );
    }
  },

  async scheduled(_controller, env, ctx): Promise<void> {
    ctx.waitUntil(refreshFeed(env));
  }
} satisfies ExportedHandler<Env>;
