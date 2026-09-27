# 🛠️ 6. Architecture, Top Bar & Sheet Design System

## 6.1 HorizontalPager Earth ↔ Moon Navigation
**Status: ✅ DONE**
- `HorizontalPager(pageCount = { 2 })` in `App.kt`.
- Page 0: `GlobeView` (Earth), Page 1: `MoonView` (Moon).
- Swipe + top-bar pill tab switching.

## 6.2 MissionControlTopBar Compact Design
**Status: ✅ DONE**
- Single 44dp floating glass bar (replaces old 130dp double-row header).
- Left: Live status badge, Center: `[ 🌍 Earth | 🌕 Moon ]` pill, Right: `⛯ Layers` button.
- Expandable glassmorphic layer quick-menu (Borders, Satellites, Hazards, Flight, Quiz).
- Layer menu hidden on Moon page.

## 6.3 Minimalist Close Button Design System
**Status: ✅ DONE**
- Created `MinimalistCloseButton.kt` (28dp frosted circle, 1.35dp stroke cross).
- Applied across all 11 sheets and cards (`CountryDossierSheet`, `MeteorologyStationSheet`, `WorldBankDashboardSheet`, `NasaCrisisMonitorSheet`, `MissionLegendSheet`, `CartographicLayersSheet`, `HazardDetailSheet`, `ISSTelemetryCard`, `MoonDetailSheet`, `FlightRouteHudCard`, and Apollo landing site cards).

## 6.4 Adaptive Large Screen & Desktop Side Sheet System
**Status: ✅ DONE**
- Shared `AdaptiveInfoSheet.kt` wrapper selects `ModalBottomSheet` on compact windows (`< 600dp`) and a 420dp right-aligned side-sheet on medium/expanded windows (`>= 600dp` desktop WASM, tablets, foldables).
