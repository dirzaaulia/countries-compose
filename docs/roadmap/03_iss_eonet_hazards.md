# 🛰️ 3. Live ISS Tracker & 🌍 4. NASA EONET Hazards

## 3. Live ISS Tracker

### 3.1 ISS Telemetry Fetch
**Status: ✅ DONE**
- `GlobeRepository.fetchISSTelemetry()` $\to$ `https://api.wheretheiss.at/v1/satellites/25544`.
- Polls every 6 seconds when Satellites layer is ON.
- Data model: `ISSTelemetry(lat, lng, altitude, velocity, visibility, timestamp)`.

### 3.2 ISS Rendering on Globe
**Status: ✅ DONE**
- 3-ring glowing beacon on Canvas at correct orbital altitude (`issRadius = currentRadius × 1.066`).
- Front-hemisphere filtering (`p.z > 0`).
- Direct screen-space and spherical proximity tap detection.
- `ISSTelemetryCard` popup on tap (velocity, altitude, coordinates, daylight/eclipse solar illumination, camera tracking).
- Floating "🛰️ ISS • [alt] km" badge next to beacon on front hemisphere.
- 92.9-minute orbital ground track path with glowing dashed cyan line.

### 3.3 ISS Badge in Top Bar
**Status: ✅ DONE**
- Real lunar distance from `moonInfo.distanceKm` passed to `MissionControlTopBar`.

---

## 4. NASA EONET Natural Hazards

### 4.1 EONET API Fetch
**Status: ✅ DONE**
- `GlobeRepository.fetchGlobalNasaEvents()` — full parse of 🔥🌀🌋🧊🌊.
- Periodic 30-minute background polling loop running in `App.kt`.
- Dynamic deduplication and real-time hazard mapping on globe.

### 4.2 Hazard Beacons on Globe
**Status: ✅ DONE**
- Pulsing beacon (animated aura + solid core) per hazard.
- Color-coded by category.
- Tap within 350km $\to$ `onHazardSelected(hazard)`.

### 4.3 Hazard Detail Sheet
**Status: ✅ DONE**
- `HazardDetailSheet.kt` — category icon, title, date, lat/lng.
- Magnitude/severity display parsed from EONET geometry (`magnitudeValue` + `magnitudeUnit`).
