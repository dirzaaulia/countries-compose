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
            
            // 2. Soft, natural planetary lighting (Half-Lambert wrap lighting)
            // Eliminates harsh flashlight falloff so continents remain bright, vibrant and clearly legible
            float NdotL = dot(N, L);
            
            // Wrapped diffuse: daylight gracefully wraps around planet with gentle ambient floor
            float wrappedDiffuse = clamp((NdotL + 0.35) / 1.35, 0.0, 1.0);
            float dayDiffuse = mix(0.38, 1.05, smoothstep(-0.15, 0.65, NdotL));
            
            // Smooth daylight to night hemisphere factor
            float sunFactor = smoothstep(-0.22, 0.18, NdotL);
            
            // Soft atmospheric twilight/sunset glow along the terminator
            float sunsetFactor = smoothstep(-0.25, -0.02, NdotL) * (1.0 - smoothstep(-0.02, 0.22, NdotL));
            vec3 sunsetColor = vec3(1.0, 0.62, 0.35); // Warm golden-orange twilight
            
            // 3. Subtle, soft cloud shadow
            vec2 shadowUV = vec2(v_TexCoordinate.x - u_SunDirection.x * 0.0025, v_TexCoordinate.y - u_SunDirection.y * 0.0025);
            float cloudShadow = texture2D(u_CloudTexture, shadowUV).r;
            vec3 litDay = dayColor.rgb * dayDiffuse * (1.0 - cloudShadow * 0.22);
            litDay += sunsetColor * (sunsetFactor * 0.18);
            
            // 4. City lights on the dark side (clean, warm, natural glow)
            vec3 litNight = nightColor.rgb * 1.35 * (1.0 - sunFactor) * (1.0 - cloudColor.r * 0.40);
            vec3 surfaceColor = mix(litNight, litDay, sunFactor);
            
            // 5. Soft, fluffy sunlit clouds
            float cloudDiffuse = mix(0.45, 1.0, smoothstep(-0.15, 0.50, NdotL));
            vec3 litCloud = vec3(0.98, 0.99, 1.0) * cloudDiffuse;
            litCloud += sunsetColor * (sunsetFactor * 0.25);
            surfaceColor = mix(surfaceColor, litCloud, cloudColor.r * sunFactor * 0.78);
            
            // 6. Ethereal atmospheric Rayleigh scattering limb glow (100% NaN-safe)
            // The atmospheric limb is only visible at the planetary horizon against space
            float NdotV = clamp(dot(N, V), 0.0, 1.0);
            float limb = smoothstep(0.45, 0.98, 1.0 - NdotV);
            float atmoGlow = limb * limb * 0.85; // Clean quadratic curve, strictly 0.0 across continents
            vec3 atmoColor = vec3(0.35, 0.68, 1.0); // Celestial atmospheric blue
            
            float atmoSun = smoothstep(-0.25, 0.45, NdotL);
            vec3 finalColor = surfaceColor + (atmoColor * atmoGlow * (atmoSun * 0.90 + 0.22));
            
            gl_FragColor = vec4(finalColor, 1.0);
        }
    """
}
