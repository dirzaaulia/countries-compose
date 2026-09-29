# 🌍 8. Country Dossier & Global Search

## 8.1 Country Dossier Material 3 Sheet
**Status: ✅ DONE**
- Compact peek mode (68dp) expanding to full M3 modal sheet.
- Decoupled network fetch from camera flight: 650ms `FastOutSlowInEasing` glide centers country first, sheet presents with skeleton loaders while live data streams in background.

## 8.2 Sub-National Administrative Divisions (ADM1 & ADM2)
**Status: RETIRED**
- The ADM explorer and GeoBoundaries integration were removed by product choice because the source did not provide a reliable parent-child administrative hierarchy.
- Country borders, selection, and the Country Dossier remain available.

## 8.3 Global Search & Instant Teleportation HUD
**Status: ✅ DONE**
- Shared `CountrySearchSheet` opens from Mission Control top bar search vector.
- In-memory index performs case- and diacritic-insensitive subsequence matching across country names, capitals, ISO-2, and ISO-3 codes.
- Selection closes sheet and triggers smooth 3D camera auto-glide to country coordinates.
