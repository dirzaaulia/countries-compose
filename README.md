# 🌍 Countries Compose

A high-performance, interactive 3D planetary exploration and geopolitical intelligence workstation built with **Compose Multiplatform (CMP)** targeting **Android** and **WebAssembly (WasmJs)**.

---

## 🧭 Project Guides & Documentation

To maintain code quality and prevent regressions across different AI models and contributors, the project is documented across three primary files:

1. **[AGENTS.md](file:///d:/Android/Projects/countries-compose/AGENTS.md)** — **Strict Agent Behavioral Rules & Critical Invariants**
   - Mandatory guidelines for all AI agents.
   - Guardrails covering 3D Euler math ($R_Y \to R_X$), Android EGL context lifecycle, WebAssembly emoji restrictions, and Material 3 modal sheet standards.

2. **[ROADMAP.md](file:///d:/Android/Projects/countries-compose/ROADMAP.md)** — **Single Source of Truth**
   - Live sprint progress, completed features, active backlog, and priority tracking.
   - Always check before starting new tasks or declaring features complete.

3. **[ARCHITECTURE.md](file:///d:/Android/Projects/countries-compose/ARCHITECTURE.md)** — **System Architecture & Pipeline**
   - Detailed breakdown of the dual-layer rendering engine (3D OpenGL ES/WebGL + 2D Compose Canvas overlay).
   - Source set map, data services, and verification instructions.

---

## 🛠️ Verification & Compilation

Always verify changes using standard compilation commands:

```bash
# Verify Android target
./gradlew :app:compileDebugKotlinAndroid

# Verify WebAssembly (WasmJs) target
./gradlew :app:compileKotlinWasmJs
```

> **Note**: Do not run `assembleDebug`, `packageDebug`, or device tests unless explicitly requested.
