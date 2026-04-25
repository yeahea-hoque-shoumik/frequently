# WavePreset.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Enum: `com.prime.frequently.data.WaveCategory`

**Summary**
Enumerates the six brainwave band categories used to classify presets and drive UI colour-coding.

| Constant | Band Range | Use Case |
|----------|-----------|----------|
| `DELTA` | 0.5–4 Hz | Sleep, healing |
| `THETA` | 4–8 Hz | Meditation, creativity |
| `ALPHA` | 8–13 Hz | Calm focus, relaxation |
| `BETA` | 13–30 Hz | Active focus, study |
| `GAMMA` | 30–100 Hz | Peak cognition |
| `SPIRITUAL` | Mixed (Alpha/Theta range) | Prayer-aligned and mindfulness presets |

---

## Class: `com.prime.frequently.data.WavePreset`

**Summary**
The immutable data model for a single binaural beat preset. Carries everything needed to display the preset in the Library, configure the audio engine, and suggest appropriate session duration.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `id` | `String` | Stable string identifier (e.g., `"delta_deep_sleep"`) used as a key in collections and history |
| `name` | `String` | Primary English display name shown in the Library and player |
| `nameArabic` | `String` | Optional Arabic name for Spiritual presets; empty string for all other categories |
| `category` | `WaveCategory` | The brainwave band this preset belongs to; drives UI colour and filter chips |
| `carrierHz` | `Double` | Base carrier frequency sent to both channels before the beat offset is applied |
| `beatHz` | `Double` | The binaural beat frequency (difference between left and right channels) |
| `description` | `String` | Short human-readable description of the preset's intended effect |
| `noiseType` | `NoiseType` | Background noise type bundled with this preset (default: NONE) |
| `noiseVolume` | `Float` | Background noise mix level at which this preset was designed (default: 0) |
| `recommendedDurationMin` | `Int` | Suggested session length in minutes shown in the UI (default: 20) |
| `colorRes` | `Int` | Optional Android color resource ID for UI tinting; 0 means use the category default |

---

_No methods — this is a pure data class._

**Insights**
- The `id` field uses a descriptive slug rather than an auto-increment integer, making presets portable and identifiable in exported data without needing a database lookup.
- `colorRes` defaults to 0 and is currently unused in code; per-category colour tinting is handled in the UI layer based on `WaveCategory`.
