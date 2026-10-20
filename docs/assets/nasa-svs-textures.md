# NASA SVS texture source audit

Status: historical SVS audit; full SVS-only coverage was not established.
The current migration uses the actual NASA Eyes assets: see
[NASA Eyes conversion and release](nasa-eyes-textures.md) for current progress.
Source pages checked 2026-10-19. Scope: 12 slots in `PlanetTextureProvider.kt`.

## Located and uploaded original maps

| Slot | SVS page | Original asset | Published dimensions | Qualification |
|---|---|---|---|---|
| Earth day | https://svs.gsfc.nasa.gov/2915/ | bluemarble-2048.png | 2048 x 1024 | Global Blue Marble composite. IDE image inspection shows a cloudless global map with terrain shading. |
| Earth night | https://svs.gsfc.nasa.gov/2916/ | earthatnight-2048.png | 2048 x 1024 | Historical global night lights; not a current Black Marble product. |
| Moon | https://svs.gsfc.nasa.gov/4720/ | lroc_color_2k.jpg | 2048 x 1024 | December 2025 color map, centered at 0 degrees; polar regions use LOLA albedo fill. Published to the runtime CDN. |
| Mercury | https://svs.gsfc.nasa.gov/11197/ | Image1_rawpng.png | 6132 x 3066 | Enhanced-color MESSENGER mosaic candidate; visual inspection and runtime conversion remain pending. |
| Jupiter | https://svs.gsfc.nasa.gov/12021/ | Hubble_Jupiter_color_global_map_2015a.tif | 3600 x 1800 | Flat global Hubble map from 2015; historical atmosphere; runtime conversion remains pending. |
| Earth clouds | https://svs.gsfc.nasa.gov/3837/ | clouds.0350.tif | 2048 x 1024 | Global cloud layer from a historical GEOS-5 model run; not MODIS or live weather. Requires decode, channel inspection, and conversion. |

Direct original URLs, obtained from SVS download listings:

- https://svs.gsfc.nasa.gov/vis/a000000/a002900/a002915/bluemarble-2048.png
- https://svs.gsfc.nasa.gov/vis/a000000/a002900/a002916/earthatnight-2048.png
- https://svs.gsfc.nasa.gov/vis/a000000/a004700/a004720/lroc_color_2k.jpg
- https://svs.gsfc.nasa.gov/vis/a010000/a011100/a011197/Image1_rawpng.png
- https://svs.gsfc.nasa.gov/vis/a010000/a012000/a012021/Hubble_Jupiter_color_global_map_2015a.tif
- https://svs.gsfc.nasa.gov/vis/a000000/a003800/a003837/frames/2048x1024_2x1_30p/Clouds/clouds.0350.tif

Credit Earth day and clouds to NASA/Goddard Space Flight Center Scientific Visualization
Studio; retain the Blue Marble attribution on its source page. Credit Earth night per its
source page. Credit Moon to NASA's Scientific Visualization Studio. Credit Mercury to
NASA's Goddard Space Flight Center and NASA/JHUAPL/Carnegie Institution of Washington.
Credit Jupiter to NASA's Goddard Space Flight Center and Space Telescope Science Institute.
SVS describes its lunar color maps as optimized for aesthetics rather than science.
Consistent hosting does not make every asset true color, current, or equally precise.

## Remaining source gaps

| Slot | Search finding | Remaining requirement |
|---|---|---|
| Mars | SVS 30338 and 1091 publish rendered globe movies/stills. | Locate an original global color map; a globe screenshot or MOLA elevation color map is not a substitute. |
| Venus | SVS 3728 publishes rendered terrain/atmosphere stills and explains that true surface color has not been mapped. | Locate a global texture and explicitly distinguish radar surface from visible cloud tops. |
| Saturn | SVS 30710 credits NASA/JPL/Space Science Institute. | A downloadable SVS global atmospheric texture has not been located. |
| Uranus | SVS 30710 points to Keck material. | A downloadable SVS global atmospheric texture has not been located. |
| Neptune | SVS 30710 points to Voyager material. | A downloadable SVS global atmospheric texture has not been located. |
| Sun | SVS 30362 provides a 360-degree Carrington EUV visualization. | Inspect map bounds: its 4104 x 2304 still is not directly a 2:1 texture; EUV is not visible true color. |

The overview https://svs.gsfc.nasa.gov/30710/ credits multiple original organizations;
it does not itself publish a complete downloadable texture pack. The search did not
establish that usable SVS-hosted maps exist for every remaining slot.

NASA JPL's separate texture catalog is outside the requested SVS-only scope:
https://maps.jpl.nasa.gov/tmaps/. Its Saturn and Neptune pages explicitly describe the
planet maps as fictional, and Uranus as a fictional solid color. They were not substituted.
A question about allowing other official NASA/JPL sources remains unanswered.

## Oracle deployment and verification

- Access: non-interactive `ssh ubuntu@footballclips-oracle` succeeded.
- Caddy Docker inspection confirmed the host root `/home/ubuntu/jarvis/storage/cloud`
  is mounted read-only at `/var/www/cloud` inside the serving container.
- Runtime texture directory: `/home/ubuntu/jarvis/storage/cloud/countries/textures`.
- Six originals uploaded to `/home/ubuntu/jarvis/storage/cloud/countries/svs-20261019-originals`.
- Original hashes are recorded in `artifacts/nasa-svs/originals/SHA256SUMS.txt`.
- All six uploaded original SHA-256 hashes were checked against local downloads.
- The Moon JPEG was decoded and visually inspected in the IDE at 2048 x 1024.
- Existing `moon.jpg` was backed up as `moon.before-svs-refresh.jpg` in the originals directory.
- The SVS Moon JPEG was published by temporary-file replacement followed by an atomic rename.
- Both public runtime mirrors returned the exact source SHA-256:
  `f7130a1822681fa7512d7dcfd40db8c10b9ba4f06777910348698260ed7a2170`.
- Primary runtime URL returned HTTP 200, `image/jpeg`, and 457942 bytes:
  https://files.fc.dirzaaulia.com/countries/textures/moon.jpg.
- The checked primary response did not include an Access-Control-Allow-Origin header;
  browser cross-origin WebGL loading remains unverified. No Caddy configuration was changed.
- Other runtime textures were not replaced. The app still has mixed-source URLs and fallbacks.
- No Kotlin, Gradle, or renderer source changes were made; compile tasks were not needed.

Remaining: finish source coverage, decode/inspect larger inputs, prepare runtime formats,
verify longitude conventions and channels, update provider URLs/fallbacks, and validate
Android/WASM compilation after source changes. Full all-planet SVS migration is not complete.
