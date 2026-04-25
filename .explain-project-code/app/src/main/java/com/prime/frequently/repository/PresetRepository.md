# PresetRepository.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.repository`

---

## Class: `com.prime.frequently.repository.PresetRepository`

**Summary**
A thin repository layer that wraps `WavePresets` constants and exposes a clean API to ViewModels. Decouples the ViewModel from the static constants object, making it straightforward to swap in a database-backed or network-backed preset source in the future.

**Instance Variables**

_No instance variables._

---

### `getAll`

**Function Name:** `com.prime.frequently.repository.PresetRepository.getAll`

**What** — Returns the complete list of all 18 built-in presets.

**Why** — Provides the full preset library to `PresetsViewModel` for display in the Library screen before any filter is applied.

**How**
1. Returns `WavePresets.ALL` directly

---

### `getByCategory`

**Function Name:** `com.prime.frequently.repository.PresetRepository.getByCategory`

**What** — Returns only the presets belonging to the specified `WaveCategory`.

**Why** — Powers the band-filter chip behaviour in the Library screen — tapping a category chip calls this to narrow the displayed list.

**How**
1. Filters `WavePresets.ALL` keeping only entries where `it.category == category`
2. Returns the filtered list

**Insights**
- The current implementation filters in memory on every call, which is fine for 18 presets but would need indexing if the preset library grew significantly.
