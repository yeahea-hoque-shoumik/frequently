# settings.gradle.kts

**Language:** Kotlin DSL (Gradle settings script)
**Package:** root project

---

## Top-Level Functions

### `pluginManagement`

**Function Name:** `settings.gradle.kts.pluginManagement`

**What** — Configures where Gradle resolves build plugins from, scoping each repository to only the artifact groups it should serve.

**Why** — Narrowing each repository with `includeGroupByRegex` prevents Gradle from querying irrelevant repositories for Android/Google/AndroidX artifacts, improving build speed and reducing the risk of resolving counterfeit packages.

**How**
1. Adds Google's Maven repository, restricted to `com.android.*`, `com.google.*`, and `androidx.*` group IDs
2. Adds Maven Central as a general fallback
3. Adds the Gradle Plugin Portal for community plugins

**Insights**
- The regex-scoped `content` blocks are a security and performance best practice; without them Gradle queries all repos for every artifact.

---

### `plugins`

**Function Name:** `settings.gradle.kts.plugins`

**What** — Applies the Foojay toolchain resolver so Gradle can automatically download the correct JDK if one is not present locally.

**Why** — Ensures reproducible builds across machines and CI without requiring developers to pre-install a specific JDK version.

**How**
1. Applies `org.gradle.toolchains.foojay-resolver-convention` at version `1.0.0`

**Insights**
- This is a settings-level plugin (applied in `settings.gradle.kts`, not `build.gradle.kts`) because it must be available before any project is configured.

---

### `dependencyResolutionManagement`

**Function Name:** `settings.gradle.kts.dependencyResolutionManagement`

**What** — Centralises all dependency repository declarations at the settings level and forbids submodules from declaring their own repositories.

**Why** — `FAIL_ON_PROJECT_REPOS` enforces a single-source-of-truth for repositories, preventing submodule `build.gradle` files from silently adding unvetted repos.

**How**
1. Sets `repositoriesMode` to `FAIL_ON_PROJECT_REPOS` — build fails if any submodule declares its own `repositories {}` block
2. Adds Google Maven for Android/AndroidX libraries
3. Adds Maven Central for general dependencies
4. Adds JitPack (`https://jitpack.io`) specifically to resolve MPAndroidChart, which is not published to Maven Central

**Insights**
- JitPack is the only non-standard repository; its presence is solely for MPAndroidChart. If that dependency is ever replaced, JitPack can be removed.
- `rootProject.name = "Frequently"` sets the project name visible in IDE and build output.
- `include(":app")` registers the single application submodule; additional modules (e.g., `:core`, `:feature`) would be added here.
