# SessionRecord.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.SessionRecord`

**Summary**
The Room entity that represents a single binaural beat listening session stored in the `sessions` table. It captures everything needed to reconstruct history stats, streak calculations, and the session list displayed in `HistoryFragment`.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `id` | `String` | UUID primary key, auto-generated on construction |
| `startTime` | `Long` | Session start timestamp in milliseconds since epoch |
| `plannedDurationSecs` | `Int` | Timer duration the user set before starting (0 = no timer) |
| `actualDurationSecs` | `Int` | Real elapsed seconds when the session ended (may be less than planned if stopped early) |
| `presetName` | `String` | Display name of the preset or "Custom" for custom Hz sessions |
| `carrierHz` | `Double` | Carrier (base) frequency used during the session |
| `beatHz` | `Double` | Binaural beat frequency used during the session |
| `noiseType` | `String` | String representation of the `NoiseType` enum value (stored as text for Room compatibility) |
| `noiseVolume` | `Float` | Background noise mix level [0.0–1.0] at the time of the session |
| `completed` | `Boolean` | `true` if the session ran to the planned timer end; `false` if stopped early |
| `intentName` | `String` | Optional session intent label (e.g., "Sleep", "Focus") — reserved for Phase 11.1 |

---

_No methods — this is a pure Room entity data class._

**Insights**
- `noiseType` is stored as a `String` rather than the `NoiseType` enum because Room does not natively serialize Kotlin enums without a TypeConverter; using a string avoids that dependency.
- `intentName` is a forward-compatibility field — it is empty for all current sessions but allows Phase 11.1 intent data to be stored without a schema migration.
- `actualDurationSecs < plannedDurationSecs` combined with `completed = false` is the canonical way to detect a stopped-early session in `HistoryViewModel`.
