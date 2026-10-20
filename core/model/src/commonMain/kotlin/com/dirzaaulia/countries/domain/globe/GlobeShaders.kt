package com.dirzaaulia.countries.domain.globe

object GlobeShaders {
    const val VERTEX_SHADER = """
        uniform mat4 u_MVPMatrix;
        uniform mat4 u_MVMatrix;
        
        attribute vec4 a_Position;
        attribute vec2 a_TexCoordinate;
        attribute vec3 a_Normal;
        
        varying vec2 v_TexCoordinate;
        varying vec3 v_Normal;
        
        void main() {
            v_TexCoordinate = a_TexCoordinate;
            v_Normal = (u_MVMatrix * vec4(a_Normal, 0.0)).xyz;
            gl_Position = u_MVPMatrix * a_Position;
        }
    """

    /**
     * Saturn Ring Shaders — NASA Cassini-mission photometric calibration.
     *
     * UV coordinate contract (set by PlanetaryRingMesh):
     *   a_TexCoord.x = radial position: 0.0 = C-Ring inner edge (74,510 km)
     *                                   1.0 = A-Ring outer edge (136,775 km)
     *
     * Ring radial positions (normalized over 74,510–136,775 km span = 62,265 km):
     *   C Ring      : 0.000 – 0.281  (74,510 – 92,000 km)
     *   B Ring      : 0.281 – 0.657  (92,000 – 117,580 km)  brightest, optically thick
     *   Cassini Div : 0.657 – 0.729  (117,580 – 122,170 km) near-empty gap
     *   A Ring      : 0.729 – 0.999  (122,170 – 136,775 km)
     *   Encke Gap   : ~0.946         (133,590 km, ~325 km wide = 0.005 normalized)
     *   Keeler Gap  : ~0.975         (136,530 km, ~35 km wide = 0.002 normalized)
     */
    const val RING_VERTEX_SHADER = """
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        uniform mat4 u_RingMVP;
        uniform float u_PlanetType;
        varying vec2 v_TexCoord;
        varying float v_PlanetType;
        void main() {
            v_TexCoord = a_TexCoord;
            v_PlanetType = u_PlanetType;
            
            vec3 pos = a_Position.xyz;
            float currentRadius = length(pos);
            if (currentRadius > 0.0) {
                float inner = 1.236;
                float outer = 2.269;
                
                if (u_PlanetType > 6.5 && u_PlanetType < 7.5) {
                    inner = 1.50;
                    outer = 2.05;
                } else if (u_PlanetType > 7.5 && u_PlanetType < 8.5) {
                    inner = 1.70;
                    outer = 2.55;
                }
                
                float targetRadius = mix(inner, outer, a_TexCoord.x);
                pos = normalize(pos) * targetRadius;
            }
            gl_Position = u_RingMVP * vec4(pos, 1.0);
        }
    """

    const val RING_FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec3 u_RingSun;
        varying vec2 v_TexCoord;
        varying float v_PlanetType;

        float gap(float r, float mid, float w) {
            float d = abs(r - mid) / (w * 0.5);
            return clamp(d * d * d, 0.0, 1.0);
        }

        float ring(float r, float mid, float w) {
            float d = abs(r - mid) / (w * 0.5);
            return 1.0 - smoothstep(0.0, 1.0, d);
        }

        void main() {
            float r = v_TexCoord.x;
            float alpha = 0.0;
            vec3 ringColor = vec3(1.0);

            if (v_PlanetType > 6.5 && v_PlanetType < 7.5) {
                // Uranus procedural rings
                float eps = ring(r, 0.94, 0.02);
                float delta = ring(r, 0.76, 0.01);
                float gamma = ring(r, 0.72, 0.01);
                float eta = ring(r, 0.69, 0.01);
                float beta = ring(r, 0.54, 0.015);
                float alphaR = ring(r, 0.49, 0.015);
                float four = ring(r, 0.45, 0.01);
                float five = ring(r, 0.41, 0.01);
                float six = ring(r, 0.36, 0.01);
                
                alpha = eps * 0.8 + (delta + gamma + eta) * 0.4 + (beta + alphaR) * 0.5 + (four + five + six) * 0.3;
                ringColor = vec3(0.5, 0.7, 0.8);
                
                if (alpha < 0.01) discard;
            } else if (v_PlanetType > 7.5 && v_PlanetType < 8.5) {
                // Neptune procedural rings
                float adams = ring(r, 0.98, 0.03);
                float arago = ring(r, 0.73, 0.01);
                float lassell = ring(r, 0.63, 0.10);
                float leVerrier = ring(r, 0.54, 0.02);
                float galle = ring(r, 0.02, 0.04);
                
                alpha = adams * 0.6 + arago * 0.3 + lassell * 0.15 + leVerrier * 0.4 + galle * 0.2;
                ringColor = vec3(0.3, 0.4, 0.6);
                
                if (alpha < 0.01) discard;
            } else {
                // Saturn
                if (r < 0.281) {
                    float fade = smoothstep(0.0, 0.04, r);
                    float inner = 0.38 + sin(r * 60.0) * 0.06;
                    alpha = fade * inner;
                } else if (r < 0.657) {
                    float pos = (r - 0.281) / (0.657 - 0.281);
                    alpha = 0.70 + sin(pos * 3.14159) * 0.22;
                } else if (r < 0.729) {
                    float pos = (r - 0.657) / (0.729 - 0.657);
                    float edge = min(smoothstep(0.0, 0.18, pos), smoothstep(1.0, 0.82, pos));
                    alpha = edge * 0.12;
                } else if (r < 0.999) {
                    float pos = (r - 0.729) / (0.999 - 0.729);
                    float aBase = 0.62 - pos * 0.18;
                    float encke = gap(r, 0.946, 0.005);
                    float keeler = gap(r, 0.975, 0.002);
                    alpha = aBase * encke * keeler;
                } else {
                    alpha = smoothstep(1.0, 0.999, r) * 0.18;
                }

                if (alpha < 0.005) discard;

                vec3 cColor = vec3(0.72, 0.68, 0.60);
                vec3 bColor = vec3(0.95, 0.92, 0.85);
                vec3 aColor = vec3(0.88, 0.84, 0.76);
                
                if (r < 0.281) {
                    ringColor = cColor;
                } else if (r < 0.657) {
                    float t = (r - 0.281) / (0.657 - 0.281);
                    ringColor = mix(bColor * 0.85, bColor, smoothstep(0.0, 0.5, t));
                } else if (r < 0.729) {
                    ringColor = vec3(0.50, 0.48, 0.42);
                } else {
                    float t = (r - 0.729) / 0.270;
                    ringColor = mix(aColor, aColor * 0.78, t);
                }
            }

            float sunlit = mix(
                clamp(-u_RingSun.y * 2.0 + 0.55, 0.35, 1.0),
                clamp( u_RingSun.y * 2.0 + 0.55, 0.35, 1.0),
                clamp(u_RingSun.y + 0.5, 0.0, 1.0)
            );
            gl_FragColor = vec4(ringColor * sunlit, alpha);
        }
    """

    const val FRAGMENT_SHADER = """
        precision mediump float;
        
        uniform sampler2D u_DayTexture;
        uniform sampler2D u_NightTexture;
        uniform sampler2D u_CloudTexture;
        uniform vec3 u_SunDirection;
        uniform float u_CloudOffset;
        uniform float u_IsMoon;
        uniform float u_IsMars;
        uniform float u_PlanetType; // 0=Earth, 1=Moon, 2=Mars, 3=Mercury, 4=Venus, 5=Jupiter, 6=Saturn, 7=Uranus, 8=Neptune
        
        varying vec2 v_TexCoordinate;
        varying vec3 v_Normal;
        
        void main() {
            vec3 N = normalize(v_Normal);
            vec3 L = normalize(u_SunDirection);
            vec3 V = vec3(0.0, 0.0, 1.0);
            
            vec4 dayColor = texture2D(u_DayTexture, v_TexCoordinate);
            float NdotL = dot(N, L);
            float diffuse = clamp(NdotL, 0.0, 1.0);
            float NdotV = clamp(dot(N, V), 0.0, 1.0);
            float limb = smoothstep(0.40, 0.98, 1.0 - NdotV);

            float pType = u_PlanetType;
            if (pType < 0.5 && u_IsMoon > 0.5) pType = 1.0;
            if (pType < 0.5 && u_IsMars > 0.5) pType = 2.0;

            if (pType > 2.5 && pType < 3.5) {
                // 3. MERCURY (NASA MESSENGER: Craters, Caloris Basin, dark night ambient)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 mercColor = mix(vec3(0.32, 0.33, 0.36), dayColor.rgb, 0.65);
                vec3 litMerc = mercColor * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                litMerc += vec3(0.55, 0.58, 0.65) * (limb * limb * 0.15);
                gl_FragColor = vec4(litMerc, 1.0);
            } else if (pType > 3.5 && pType < 4.5) {
                // 4. VENUS (NASA Magellan/Pioneer: Swirling sulfur clouds)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 sulfurCloud = mix(vec3(0.92, 0.78, 0.42), dayColor.rgb, 0.5);
                vec3 venusLit = sulfurCloud * (diffuse * sunFactor + 0.10 * (1.0 - sunFactor));
                vec3 glowColor = vec3(0.98, 0.82, 0.45);
                venusLit += glowColor * (limb * limb * 0.25);
                gl_FragColor = vec4(venusLit, 1.0);
            } else if (pType > 4.5 && pType < 5.5) {
                // 5. JUPITER: retain the NASA Eyes map's bands and Great Red Spot.
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 litJup = dayColor.rgb * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                litJup += vec3(0.90, 0.80, 0.60) * (limb * limb * 0.18);
                gl_FragColor = vec4(litJup, 1.0);
            } else if (pType > 5.5 && pType < 6.5) {
                // 6. SATURN (NASA Cassini-Huygens: 2K texture map + soft limb)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 litSat = dayColor.rgb * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                litSat += vec3(0.95, 0.85, 0.55) * (limb * limb * 0.18);
                gl_FragColor = vec4(litSat, 1.0);
            } else if (pType > 6.5 && pType < 7.5) {
                // 7. URANUS (NASA Voyager 2 / Hubble: 2K Cyan Methane texture map + atmosphere)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 litUranus = dayColor.rgb * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                litUranus += vec3(0.50, 0.95, 0.90) * (limb * limb * 0.25);
                gl_FragColor = vec4(litUranus, 1.0);
            } else if (pType > 7.5 && pType < 8.5) {
                // 8. NEPTUNE (NASA Voyager 2 / Hubble: 2K Azure Blue texture map + limb glow)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 litNep = dayColor.rgb * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                litNep += vec3(0.35, 0.65, 1.0) * (limb * limb * 0.28);
                gl_FragColor = vec4(litNep, 1.0);
            } else if (pType > 0.5 && pType < 1.5) {
                // 1. MOON (NASA LRO / Apollo: Basaltic Maria, Anorthositic Highlands)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                float texLum = dot(dayColor.rgb, vec3(0.299, 0.587, 0.114));
                vec3 mareBasalt = vec3(0.24, 0.25, 0.27);
                vec3 highlandAnorthosite = vec3(0.82, 0.82, 0.85);
                vec3 lunarSurface = mix(mareBasalt, highlandAnorthosite, smoothstep(0.15, 0.65, texLum));
                lunarSurface = mix(lunarSurface, dayColor.rgb, 0.65);
                vec3 litMoon = lunarSurface * (diffuse * sunFactor + 0.06 * (1.0 - sunFactor));
                litMoon += vec3(0.75, 0.78, 0.85) * (limb * limb * 0.15);
                gl_FragColor = vec4(litMoon, 1.0);
            } else if (pType > 1.5 && pType < 2.5) {
                // 2. MARS (NASA MRO / Viking: Ferric Iron Oxide, Volcanic Basalts)
                float sunFactor = smoothstep(-0.20, 0.08, NdotL);
                vec3 rustRed = vec3(0.82, 0.36, 0.18);
                vec3 darkBasalt = vec3(0.35, 0.18, 0.12);
                float texLum = dot(dayColor.rgb, vec3(0.299, 0.587, 0.114));
                vec3 marsSurface = mix(darkBasalt, rustRed, smoothstep(0.12, 0.75, texLum));
                marsSurface = mix(marsSurface, dayColor.rgb, 0.55);
                vec3 litMars = marsSurface * (diffuse * sunFactor + 0.08 * (1.0 - sunFactor));
                float atmoGlow = limb * limb * 0.55;
                vec3 atmoColor = vec3(0.88, 0.52, 0.32);
                float atmoSun = smoothstep(-0.15, 0.35, NdotL);
                vec3 finalColor = litMars + (atmoColor * atmoGlow * (atmoSun * 0.85 + 0.15));
                gl_FragColor = vec4(finalColor, 1.0);
            } else {
                // 0. EARTH
                vec4 nightColor = texture2D(u_NightTexture, v_TexCoordinate);
                vec4 cloudColor = texture2D(u_CloudTexture, v_TexCoordinate);
                float sunFactor = smoothstep(-0.06, 0.06, NdotL);
                float dayDiffuse = mix(0.40, 1.05, clamp((NdotL + 0.15) / 1.15, 0.0, 1.0));
                float sunsetFactor = smoothstep(-0.08, -0.01, NdotL) * (1.0 - smoothstep(-0.01, 0.08, NdotL));
                vec3 sunsetColor = vec3(1.0, 0.58, 0.28);
                vec2 shadowUV = vec2(v_TexCoordinate.x - u_SunDirection.x * 0.0025, v_TexCoordinate.y - u_SunDirection.y * 0.0025);
                float cloudShadow = texture2D(u_CloudTexture, shadowUV).r;
                vec3 litDay = dayColor.rgb * dayDiffuse * (1.0 - cloudShadow * 0.22);
                litDay += sunsetColor * (sunsetFactor * 0.35);
                float nightFactor = 1.0 - sunFactor;
                vec3 nightLandAmbient = dayColor.rgb * 0.16;
                vec3 litNight = (nightColor.rgb * 1.5 + nightLandAmbient) * nightFactor * (1.0 - cloudColor.r * 0.35);
                vec3 surfaceColor = mix(litNight, litDay, sunFactor);
                float cloudDiffuse = mix(0.45, 1.0, smoothstep(-0.05, 0.35, NdotL));
                vec3 litCloud = vec3(0.98, 0.99, 1.0) * cloudDiffuse;
                litCloud += sunsetColor * (sunsetFactor * 0.45);
                surfaceColor = mix(surfaceColor, litCloud, cloudColor.r * sunFactor * 0.82);
                float atmoGlow = limb * limb * 0.85;
                vec3 atmoColor = vec3(0.35, 0.68, 1.0);
                float atmoSun = smoothstep(-0.15, 0.35, NdotL);
                vec3 finalColor = surfaceColor + (atmoColor * atmoGlow * (atmoSun * 0.85 + 0.15));
                gl_FragColor = vec4(finalColor, 1.0);
            }
        }
    """
}
