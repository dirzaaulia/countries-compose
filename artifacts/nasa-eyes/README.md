# NASA Eyes texture conversion workspace

Sources: https://eyes.nasa.gov/assets/static/maps/
Outputs: 2048 x 1024 longitude/latitude maps for Countries' existing sphere UVs.
Source cube faces and generated maps are separate from application source files.

- `cube-faces/`: 72 decoded source images (six faces for each of 12 layers).
- `eyes-v1/`: 12 JPEGs, `SHA256SUMS.txt`, and per-face `SOURCES.tsv`.
- `contact-sheet.png`: reviewed preview of all converted maps.
- `pipeline-classes/`: local Java build output, not part of the server release.
- `Caddyfile.before` and `Caddyfile.candidate`: private deployment review files.

Rebuild with JDK 17 or newer from the project root:

```powershell
javac -d artifacts/nasa-eyes/pipeline-classes tools/textures/CubeProjection.java tools/textures/CubeProjectionChecks.java tools/textures/EyesTexturePipeline.java tools/textures/EyesReleaseChecks.java
java '-Djava.awt.headless=true' -cp artifacts/nasa-eyes/pipeline-classes EyesTexturePipeline artifacts/nasa-eyes
```

After upload and Caddy reload, verify both mirrors with:

```powershell
java '-Djava.awt.headless=true' -cp artifacts/nasa-eyes/pipeline-classes EyesReleaseChecks artifacts/nasa-eyes/eyes-v1
```

Conversion and Oracle deployment completed on 2026-10-19. Android and Web compilation
and module Spotless checks passed. Both public mirrors passed all 12 image hash, MIME,
dimension, CORS, immutable-cache, and OPTIONS checks. The app's eyes-v1 URLs are live.
On-device/browser visual testing has not been performed.
See [release documentation](../../docs/assets/nasa-eyes-textures.md).
