package com.dirzaaulia.countries

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
        
        varying vec2 v_TexCoordinate;
        varying vec3 v_Normal;
        
        void main() {
            vec3 N = normalize(v_Normal);
            vec3 L = normalize(u_SunDirection);
            vec3 V = vec3(0.0, 0.0, 1.0); // View vector pointing towards camera in orthographic space
            
            // 1. Textures
            vec4 dayColor = texture2D(u_DayTexture, v_TexCoordinate);
            vec4 nightColor = texture2D(u_NightTexture, v_TexCoordinate);
            vec4 cloudColor = texture2D(u_CloudTexture, v_TexCoordinate);
            
            // 2. Realistic planetary lighting with curved spherical terminator
            float NdotL = dot(N, L);
            
            // Crisp, natural terminator transition (~6 deg civil twilight arc)
            float sunFactor = smoothstep(-0.06, 0.06, NdotL);
            
            // Physically inspired daylight illumination with smooth spherical cosine curve
            float dayDiffuse = mix(0.40, 1.05, clamp((NdotL + 0.15) / 1.15, 0.0, 1.0));
            
            // Ethereal golden-amber sunset/sunrise twilight arc along the curved terminator
            float sunsetFactor = smoothstep(-0.08, -0.01, NdotL) * (1.0 - smoothstep(-0.01, 0.08, NdotL));
            vec3 sunsetColor = vec3(1.0, 0.58, 0.28); // Warm atmospheric twilight glow
            
            // 3. Cloud shadows on the sunlit hemisphere
            vec2 shadowUV = vec2(v_TexCoordinate.x - u_SunDirection.x * 0.0025, v_TexCoordinate.y - u_SunDirection.y * 0.0025);
            float cloudShadow = texture2D(u_CloudTexture, shadowUV).r;
            vec3 litDay = dayColor.rgb * dayDiffuse * (1.0 - cloudShadow * 0.22);
            litDay += sunsetColor * (sunsetFactor * 0.35);
            
            // 4. Brilliant city lights strictly on the dark side (clean, high contrast)
            float nightFactor = 1.0 - sunFactor;
            vec3 litNight = nightColor.rgb * 1.45 * nightFactor * (1.0 - cloudColor.r * 0.45);
            vec3 surfaceColor = mix(litNight, litDay, sunFactor);
            
            // 5. Soft sunlit clouds with sunset fringe along the terminator
            float cloudDiffuse = mix(0.45, 1.0, smoothstep(-0.05, 0.35, NdotL));
            vec3 litCloud = vec3(0.98, 0.99, 1.0) * cloudDiffuse;
            litCloud += sunsetColor * (sunsetFactor * 0.45);
            surfaceColor = mix(surfaceColor, litCloud, cloudColor.r * sunFactor * 0.82);
            
            // 6. Ethereal atmospheric Rayleigh scattering limb glow (100% NaN-safe)
            // The atmospheric limb is only visible at the planetary horizon against space
            float NdotV = clamp(dot(N, V), 0.0, 1.0);
            float limb = smoothstep(0.45, 0.98, 1.0 - NdotV);
            float atmoGlow = limb * limb * 0.85; // Clean quadratic curve, strictly 0.0 across continents
            vec3 atmoColor = vec3(0.35, 0.68, 1.0); // Celestial atmospheric blue
            
            float atmoSun = smoothstep(-0.15, 0.35, NdotL);
            vec3 finalColor = surfaceColor + (atmoColor * atmoGlow * (atmoSun * 0.85 + 0.15));
            
            gl_FragColor = vec4(finalColor, 1.0);
        }
    """
}
