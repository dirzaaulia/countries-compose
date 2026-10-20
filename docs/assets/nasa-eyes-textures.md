# NASA Eyes planetary texture discovery

Status: asset discovery complete for default color cube maps for all eight planets,
Sun, and Moon. All 60 maximum-listed color faces returned HTTP 200 with image content types.
Also verified 24 Earth auxiliary faces and four planetary ring textures (88 asset URLs total).
Checked 2026-10-19. These checks establish asset availability, not decoded image integrity.

## Direct evidence

- Application: https://eyes.nasa.gov/apps/solar-system/
- Runtime configuration: https://eyes.nasa.gov/apps/solar-system/config.js
- Application bundle: https://eyes.nasa.gov/apps/solar-system/app.js

`config.js` sets `staticAssetsUrl` to `https://eyes.nasa.gov/assets/static` and
`dynamicAssetsUrl` to `https://eyes.nasa.gov/assets/dynamic`.
The application creates each body's `spheroidLOD`, explicitly chooses cube mapping,
and resolves its texture path under `$STATIC_ASSETS_URL/maps/`.
`SpheroidLODComponent` substitutes numeric face indices 0 through 5 into `$FACE`.
`TextureLOD` substitutes one of the declared sizes into `$SIZE`.

These are the actual default assets referenced by NASA Eyes, rather than guessed SVS URLs
or an assumption that the older JPL Solar System Simulator catalog supplies Eyes.

## Color maps

Base URL: `https://eyes.nasa.gov/assets/static/maps/`
Append `{body}/color_{size}_{face}.{extension}` with face 0 through 5.

| Body | Declared face sizes | Extension | Largest verified face size |
|---|---|---|---|
| Sun | 4, 512 | png | 512 |
| Mercury | 4, 512, 4096 | png | 4096 |
| Venus | 4, 512 | png | 512 |
| Earth | 16, 512, 4096 | png | 4096 |
| Moon | 16, 512, 4096 | png | 4096 |
| Mars | 4, 512, 4096 | png | 4096 |
| Jupiter | 16, 512, 4096 | jpg | 4096 |
| Saturn | 4, 512 | png | 512 |
| Uranus | 4, 256 | png | 256 |
| Neptune | 4, 256 | png | 256 |

Direct examples (each is one face, not a complete globe texture):

- https://eyes.nasa.gov/assets/static/maps/mercury/color_4096_0.png
- https://eyes.nasa.gov/assets/static/maps/venus/color_512_0.png
- https://eyes.nasa.gov/assets/static/maps/earth/color_4096_0.png
- https://eyes.nasa.gov/assets/static/maps/mars/color_4096_0.png
- https://eyes.nasa.gov/assets/static/maps/jupiter/color_4096_0.jpg
- https://eyes.nasa.gov/assets/static/maps/saturn/color_512_0.png
- https://eyes.nasa.gov/assets/static/maps/uranus/color_256_0.png
- https://eyes.nasa.gov/assets/static/maps/neptune/color_256_0.png
- https://eyes.nasa.gov/assets/static/maps/sun/color_512_0.png
- https://eyes.nasa.gov/assets/static/maps/moon/color_4096_0.png

Machine-readable body/resolution inventory: [nasa-eyes-textures.json](nasa-eyes-textures.json).
Earth auxiliary maps, rings, and cube orientation:
[nasa-eyes-auxiliary-textures.json](nasa-eyes-auxiliary-textures.json).

## Additional layers

Earth provides `night`, `cloud`, `normal`, and `specular` layers under `maps/earth/`,
using the same `{layer}_{size}_{face}.png` naming and sizes 16, 512, and 4096.
All six 4096 faces for each of these four layers were checked successfully.

Ring sprites under `https://eyes.nasa.gov/assets/static/sprites/`:

- `saturn_rings_top.png`
- `saturn_rings_bottom.png`
- `uranus_rings.png`
- `neptune_rings.png`

## Provenance and integration limits

Verified: these files are hosted by NASA and referenced by the NASA Eyes application.
Not established: original mission/product ID, observation date, image processing history,
and separate image contributor credit for every map. The bundle's general Eyes team credits
list artists and developers but do not provide a per-texture scientific provenance table.
The Earth, Saturn, and Neptune description JSON files inspected contain planet information
and exploration history, not texture-specific source attribution.

The listed resolutions describe default cube-map layers. Eyes also contains CMTS/WMTS
surface rendering paths; these tables do not claim to inventory every optional surface mode.
These runtime URLs may change with future application deployments.

## Countries conversion and integration

Converted 12 maps: all eight planets, Sun, Moon, Earth night lights, and Earth clouds.
Outputs are 2048 x 1024 JPEGs in `artifacts/nasa-eyes/eyes-v1/`.
Inputs use six 512-pixel faces per layer, or 256-pixel faces for Uranus and Neptune.
The latter retain their source detail; producing a 2K output does not add native detail.

The reproducible Java pipeline is in `tools/textures/`. It uses the cube frames above,
pixel-center sampling, and bilinear taps across neighboring faces. Longitude -180 degrees
maps to u=0, Greenwich to u=0.5, and north to v=0, matching Countries' existing sphere UVs.
Cloud output is grayscale source luminance multiplied by alpha for the shader's red-channel mask.

Completed checks: 175 analytic direction samples including poles, antimeridian, and cube seams;
cloud alpha-mask test; JPEG decoding and dimensions for all 12 outputs; Earth geographic
land/ocean orientation checks. The generated contact sheet was visually reviewed.
`SHA256SUMS.txt` records output hashes; `SOURCES.tsv` records each input URL and hash.

App integration now targets the versioned `countries/textures/eyes-v1/` release on both
Oracle mirrors. Mixed-source fallbacks were removed. WebGL loads Earth's real night/cloud
layers with a black placeholder while unavailable. Jupiter uses the NASA map without adding
a synthetic Red Spot. Camera transforms and Android EGL lifecycle settings are preserved.

Verification passed: Spotless checks for `:core:network`, `:core:model`, `:feature:solarsystem`,
and `:composeApp`; `:composeApp:compileAndroidMain`; `:composeApp:compileKotlinWasmJs`.
The AGP KMP module exposes `compileAndroidMain`, rather than `compileDebugKotlinAndroid`.
Existing project configuration/deprecation warnings remain. No APK or device tests were run.

## Oracle deployment: complete, 2026-10-19

Published to `/home/ubuntu/jarvis/storage/cloud/countries/textures/eyes-v1/`.
All 12 uploaded map hashes passed server-side verification before publication.

Public mirrors:

- https://files.fc.dirzaaulia.com/countries/textures/eyes-v1/
- https://files.168.110.196.30.sslip.io/countries/textures/eyes-v1/

Append a filename such as `earth_day.jpg`, `mars.jpg`, or `jupiter.jpg`.
`SHA256SUMS.txt` and `SOURCES.tsv` are published alongside the images.

`EyesReleaseChecks` passed 24 public image downloads: exact SHA-256 matches, JPEG MIME,
2048 x 1024 decoding, wildcard CORS, and one-year immutable caching. OPTIONS preflight passed
on both mirrors. Caddy configuration was validated before reload. Its previous configuration
is backed up at `/home/ubuntu/jarvis/Caddyfile.before-eyes-v1`; earlier texture paths remain
available. The app's versioned URLs now resolve to the verified release.

Recommended next validation: visually inspect the planet views on Android and Web, especially
Earth day/night/cloud alignment and Jupiter. Automated asset/compile checks do not establish
on-device rendering quality. Solar overview symbols and procedural ring rendering were unchanged.
