# FrequencyJourney.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.FrequencyJourney`

**Summary**
A data class representing a user-defined multi-segment audio journey — a named sequence of `FrequencyWaypoint` segments that the audio engine will play through in order. Reserved for Phase 11.2 (progressive frequency transitions) and Phase 11.3 (Journey Builder).

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `id` | `String` | Unique identifier auto-generated as a UUID; used as the primary key when persisted |
| `name` | `String` | User-facing title of the journey (e.g., "Morning Focus Ramp") |
| `waypoints` | `List<FrequencyWaypoint>` | Ordered list of frequency segments that form the journey timeline |
| `noiseType` | `NoiseType` | Background noise type applied to the entire journey (default: NONE) |
| `noiseVolume` | `Float` | Mix level of background noise across all segments (default: 0) |

---

_No methods — this is a pure data class._

**Insights**
- `noiseType` and `noiseVolume` are journey-level settings, meaning the same noise backdrop plays across all waypoints; per-waypoint noise overrides are not modelled here.
- The UUID default for `id` means each `FrequencyJourney()` call produces a globally unique identity without requiring a database auto-increment.
