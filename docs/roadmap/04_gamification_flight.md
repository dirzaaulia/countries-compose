# 🎮 5. Gamification & Supersonic Flight Simulator

## 5.1 Geography Quiz Mode
**Status: ✅ DONE**
- `QuizHudCard.kt` — target country, score + streak, feedback messages.
- +100pts + (streak × 25) bonus; streak reset on wrong answer.
- Next Question / End Quiz; mutually exclusive with Flight Mode.
- **Target country silhouette glow**: Pulsing emerald green aura on correct answer and golden amber glow on target country in `GlobeView.kt`.

## 5.2 Supersonic Commercial Aircraft Animation Engine
**Status: ✅ DONE**
- `FlightRouteHudCard.kt` — origin $\to$ destination with km distance.
- `AstronomyMath.calculateGreatCircleArc()` + `calculateGreatCircleDistance()`.
- Glowing amber dashed arc + auto fly-to on activation.
- **Dynamic Tangent Heading (Bearing)**: Vector plane icon points along geodesic tangent: $\theta = \text{atan2}(p_{y2} - p_{y1}, p_{x2} - p_{x1})$.
- **Parabolic Cruising Altitude Arc**: Realistic climb, cruise, and descent: $r(t) = R_{\text{globe}} \times (1.012 + 0.026 \cdot \sin(t \cdot \pi))$.
- **Fading Contrail & Strobe Lights**: 6-segment jet exhaust trail fading smoothly from $55\%$ opacity down to $0\%$. Alternating port (red) and starboard (green) navigation strobes blinking on wingtips.
- **Mach 2.2 SST Speed Metrics**: Toggle between commercial airliner (Mach 0.78 / 850 km/h) and supersonic Concorde corridor (Mach 2.2 / 2,335 km/h).
- **Interactive Country Selector**: Direct tap-to-change departure and arrival country selection chips.
