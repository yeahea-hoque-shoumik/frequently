# build.gradle.kts

**Language:** Kotlin DSL (Gradle build script)
**Package:** root project

---

## Top-Level Functions

### `plugins`

**Function Name:** `build.gradle.kts.plugins`

**What** — Declares the root-level Gradle plugin dependencies without applying them to this project directly.

**Why** — In a multi-module Android project the root `build.gradle.kts` is the single place to declare plugin versions. Using `apply false` makes the versions available to submodules without actually activating them at the root level, preventing classpath conflicts.

**How**
1. References the Android Gradle Plugin (AGP) via `libs.plugins.android.application` with `apply false`
2. References the Kotlin Android plugin via `libs.plugins.kotlin.android` with `apply false`
3. References the KSP (Kotlin Symbol Processing) plugin via `libs.plugins.ksp` with `apply false`

**Insights**
- All three entries use version catalog aliases (`libs.plugins.*`), meaning actual version numbers live in `gradle/libs.versions.toml` — a single source of truth for dependency versions.
- `apply false` is the standard pattern for root build files: declare here, apply in each submodule (`app/build.gradle.kts`) as needed.
- KSP replaces the older `kapt` annotation processor; its presence here reflects the project's use of Room with Kotlin 2.x.
