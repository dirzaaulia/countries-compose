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
                // 3. MERCURY (NASA MESSENGER: Craters, Caloris Basin, enhanced night ambient)
                float sunFactor = smoothstep(-0.35, 0.10, NdotL);
                vec3 mercColor = mix(vec3(0.32, 0.33, 0.36), dayColor.rgb, 0.65);
                float mercLight = mix(0.42, 1.0, diffuse);
                vec3 litMerc = mercColor * (mercLight * sunFactor + 0.42 * (1.0 - sunFactor));
                litMerc += vec3(0.55, 0.58, 0.65) * (limb * limb * 0.20);
                gl_FragColor = vec4(litMerc, 1.0);
            } else if (pType > 3.5 && pType < 4.5) {
                // 4. VENUS (NASA Magellan/Pioneer: Swirling sulfur clouds, luminous atmosphere)
                float sunFactor = smoothstep(-0.32, 0.12, NdotL);
                vec3 sulfurCloud = mix(vec3(0.92, 0.78, 0.42), dayColor.rgb, 0.5);
                vec3 venusLit = sulfurCloud * (mix(0.45, 1.0, diffuse) * sunFactor + 0.45 * (1.0 - sunFactor));
                vec3 glowColor = vec3(0.98, 0.82, 0.45);
                venusLit += glowColor * (limb * limb * 0.35);
                gl_FragColor = vec4(venusLit, 1.0);
            } else if (pType > 4.5 && pType < 5.5) {
                // 5. JUPITER (NASA Cassini/Juno: Cloud bands, Great Red Spot, soft shadow)
                float sunFactor = smoothstep(-0.32, 0.10, NdotL);
                float lat = (v_TexCoordinate.y - 0.5) * 2.0; // -1 to 1
                float bandPattern = sin(lat * 32.0) * 0.12 + cos(lat * 16.0) * 0.08;
                vec3 bandCream = vec3(0.91, 0.82, 0.68);
                vec3 bandBrown = vec3(0.72, 0.48, 0.28);
                vec3 jupTex = mix(bandBrown, bandCream, smoothstep(-0.3, 0.3, bandPattern));
                jupTex = mix(jupTex, dayColor.rgb, 0.5);

                // Great Red Spot at lat ~ -0.22, lon ~ 0.45
                vec2 spotCenter = vec2(0.45, 0.39);
                float distSpot = length((v_TexCoordinate - spotCenter) * vec2(2.5, 1.0));
                if (distSpot < 0.08) {
                    jupTex = mix(vec3(0.85, 0.22, 0.15), jupTex, smoothstep(0.02, 0.08, distSpot));
                }
                vec3 litJup = jupTex * (mix(0.45, 1.0, diffuse) * sunFactor + 0.42 * (1.0 - sunFactor));
                litJup += vec3(0.90, 0.80, 0.60) * (limb * limb * 0.22);
                gl_FragColor = vec4(litJup, 1.0);
            } else if (pType > 5.5 && pType < 6.5) {
                // 6. SATURN (NASA Cassini-Huygens: Golden bands, soft night illumination)
                float sunFactor = smoothstep(-0.32, 0.10, NdotL);
                vec3 butterscotch = vec3(0.92, 0.82, 0.52);
                vec3 satTex = mix(butterscotch, dayColor.rgb, 0.5);
                vec3 litSat = satTex * (mix(0.45, 1.0, diffuse) * sunFactor + 0.42 * (1.0 - sunFactor));
                litSat += vec3(0.95, 0.85, 0.55) * (limb * limb * 0.22);
                gl_FragColor = vec4(litSat, 1.0);
            } else if (pType > 6.5 && pType < 7.5) {
                // 7. URANUS (NASA Voyager 2/Hubble: Cyan Methane haze, luminous limb)
                float sunFactor = smoothstep(-0.32, 0.12, NdotL);
                vec3 cyanHaze = vec3(0.42, 0.88, 0.82);
                vec3 uranusTex = mix(cyanHaze, dayColor.rgb, 0.3);
                vec3 litUranus = uranusTex * (mix(0.45, 1.0, diffuse) * sunFactor + 0.45 * (1.0 - sunFactor));
                litUranus += vec3(0.50, 0.95, 0.90) * (limb * limb * 0.35);
                gl_FragColor = vec4(litUranus, 1.0);
            } else if (pType > 7.5 && pType < 8.5) {
                // 8. NEPTUNE (NASA Voyager 2/Hubble: Azure blue, Great Dark Spot, soft night)
                float sunFactor = smoothstep(-0.32, 0.12, NdotL);
                vec3 azureBlue = vec3(0.22, 0.42, 0.92);
                vec3 nepTex = mix(azureBlue, dayColor.rgb, 0.4);

                // Great Dark Spot at ~ (0.35, 0.32)
                float distDark = length((v_TexCoordinate - vec2(0.35, 0.32)) * vec2(2.0, 1.0));
                if (distDark < 0.07) {
                    nepTex = mix(vec3(0.08, 0.18, 0.52), nepTex, smoothstep(0.01, 0.07, distDark));
                }
                vec3 litNep = nepTex * (mix(0.45, 1.0, diffuse) * sunFactor + 0.45 * (1.0 - sunFactor));
                litNep += vec3(0.35, 0.65, 1.0) * (limb * limb * 0.40);
                gl_FragColor = vec4(litNep, 1.0);
            } else if (pType > 0.5 && pType < 1.5) {
                // 1. MOON
                float sunFactor = smoothstep(-0.25, 0.10, NdotL);
                float lunarLight = mix(0.35, 0.95, diffuse);
                vec3 litMoon = dayColor.rgb * (lunarLight * sunFactor + 0.35 * (1.0 - sunFactor));
                gl_FragColor = vec4(litMoon, 1.0);
            } else if (pType > 1.5 && pType < 2.5) {
                // 2. MARS
                float sunFactor = smoothstep(-0.25, 0.10, NdotL);
                vec3 rustRed = vec3(0.78, 0.32, 0.16);
                vec3 darkCanyon = vec3(0.42, 0.16, 0.09);
                float texLum = dot(dayColor.rgb, vec3(0.299, 0.587, 0.114));
                vec3 marsSurface = mix(darkCanyon, rustRed, smoothstep(0.1, 0.8, texLum));
                float marsLight = mix(0.38, 1.0, diffuse);
                vec3 litMars = marsSurface * (marsLight * sunFactor + 0.38 * (1.0 - sunFactor));
                float atmoGlow = limb * limb * 0.55;
                vec3 atmoColor = vec3(0.85, 0.48, 0.25);
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
