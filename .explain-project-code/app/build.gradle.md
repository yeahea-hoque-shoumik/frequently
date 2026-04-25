# app/build.gradle.kts

**Language:** Kotlin DSL (Gradle application module build script)
**Package:** `com.prime.frequently`

---

## Top-Level Blocks

### `plugins`

**Function Name:** `app/build.gradle.kts.plugins`

**What** — Applies the Android Application and KSP plugins to this module.

**Why** — The Android Application plugin enables APK/AAB generation; KSP provides compile-time annotation processing for Room without the overhead of `kapt`.

**How**
1. Applies `libs.plugins.android.application` — the AGP plugin that turns this module into a deployable Android app
2. (Commented out) `libs.plugins.kotlin.android` — not needed because AGP 9.x infers Kotlin support from KSP being present
3. Applies `libs.plugins.ksp` — Kotlin Symbol Processing for Room's `@Dao`/`@Entity`/`@Database` code generation

**Insights**
- The Kotlin Android plugin alias is intentionally commented out. AGP 9.1+ can configure Kotlin compilation automatically when KSP is applied; keeping it avoids duplicate plugin configuration errors.

---

### `android`

**Function Name:** `app/build.gradle.kts.android`

**What** — Configures all Android-specific build settings: SDK versions, app identity, build types, and Java compatibility.

**Why** — These settings define what API surface the app can use (compileSdk 36), the minimum device requirement (minSdk 33 / Android 13), and the app's identity on the Play Store.

**How**
1. Sets `namespace` to `com.prime.frequently` — used for R class generation and manifest merging
2. Configures `compileSdk` as release 36 with `minorApiLevel = 1` — targets the latest stable SDK
3. In `defaultConfig`: sets `applicationId`, `minSdk = 33`, `targetSdk = 36`, `versionCode = 1`, `versionName = "1.0"`, and registers the standard instrumentation test runner
4. In `buildTypes.release`: disables minification (`isMinifyEnabled = false`) and references ProGuard rules files
5. Enables `viewBinding = true` so layout XML files generate type-safe binding classes
6. Sets Java source and target compatibility to `VERSION_11`

**Insights**
- `minSdk = 33` (Android 13) is a high floor — it excludes ~30% of active devices but allows use of modern APIs without legacy fallbacks.
- `isMinifyEnabled = false` means no code shrinking in release builds; this is fine during development but should be revisited before a Play Store release.
- `kotlinOptions { jvmTarget }` is deliberately absent — AGP 9.x propagates `compileOptions` Java version to Kotlin automatically.

---

### `dependencies`

**Function Name:** `app/build.gradle.kts.dependencies`

**What** — Declares all runtime and compile-time dependencies the app module needs.

**Why** — Each dependency group corresponds to a feature area: architecture, persistence, navigation, async, charts, and audio-domain logic.

**How**
1. **Core**: `core-ktx`, `appcompat`, `material`, `activity`, `constraintlayout` — baseline Android UI and Kotlin extensions
2. **Architecture**: `lifecycle-viewmodel`, `lifecycle-livedata`, `fragment-ktx` — MVVM support
3. **Room**: `room-runtime` + `room-ktx` at runtime; `room-compiler` via `ksp()` for code generation
4. **Navigation**: `navigation-fragment` + `navigation-ui` — Jetpack Navigation Component
5. **Coroutines**: `kotlinx-coroutines-android` — structured concurrency for audio threads and DB queries
6. **Charts**: `mpandroidchart` — session history bar charts (sourced from JitPack)
7. **Prayer times**: `adhan` — optional Hijri/prayer-aligned preset calculations
8. **Preferences**: `androidx.preference` — Settings screen via `PreferenceFragmentCompat`
9. **Tests**: JUnit, AndroidX JUnit, Espresso

**Insights**
- Room uses `ksp()` (not `implementation()`), which means the Room compiler runs at build time only and produces no runtime overhead.
- `adhan` is marked optional in comments; if prayer-aligned presets (Phase 11.4) are cut, this can be removed.
- No `INTERNET` permission and no network dependency (Retrofit, OkHttp, Firebase) — consistent with the app's privacy-first design.
