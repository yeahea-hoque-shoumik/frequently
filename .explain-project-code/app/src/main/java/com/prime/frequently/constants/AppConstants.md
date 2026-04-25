# AppConstants.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.constants`

---

## Class: `com.prime.frequently.constants.AppConstants`

**Summary**
A singleton constants object that centralises every magic number, string key, and configuration value used across the app. Grouping them here ensures a single source of truth for audio parameters, UI behaviour, database naming, and preference keys.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `SAMPLE_RATE` | `Int` | PCM sample rate: 44100 Hz — the standard audio CD rate, compatible with all Android hardware |
| `BUFFER_FRAMES` | `Int` | Number of audio frames per render cycle (4096); balances latency against CPU overhead |
| `FADE_DURATION_MS` | `Int` | Envelope fade in/out duration: 500 ms — long enough to be click-free, short enough to feel responsive |
| `MIN_SESSION_SECS_FOR_STREAK` | `Int` | Minimum session length (300 s / 5 min) that counts toward the daily habit streak |
| `CARRIER_HZ_MIN` | `Double` | Lowest allowed carrier (base) frequency: 100 Hz |
| `CARRIER_HZ_MAX` | `Double` | Highest allowed carrier frequency: 500 Hz |
| `BEAT_HZ_MIN` | `Double` | Smallest binaural beat frequency: 0.5 Hz |
| `BEAT_HZ_MAX` | `Double` | Largest binaural beat frequency: 100 Hz |
| `NOTIFICATION_CHANNEL_ID` | `String` | Notification channel identifier for background playback service |
| `NOTIFICATION_ID` | `Int` | Integer ID for the persistent playback notification (1001) |
| `DB_NAME` | `String` | Room database file name: `"frequently.db"` |
| `TIMER_PRESET_MINUTES` | `List<Int>` | Timer quick-select chips shown in the UI: 5, 10, 20, 30, 45, 60, 90 minutes |
| `PREF_KEEP_SCREEN_ON` | `String` | SharedPreferences key for the "keep screen on during session" toggle |
| `PREF_HEADPHONE_WARNING` | `String` | Key for the "warn if headphones not connected" toggle |
| `PREF_CARRIER_HZ` | `String` | Key for the user's preferred default carrier frequency |
| `PREF_NOISE_TYPE` | `String` | Key for the user's preferred background noise type |
| `PREF_SAMPLE_RATE` | `String` | Key for the advanced sample-rate override setting |
| `PREF_AUTO_FADE` | `String` | Key for the "automatically fade out at session end" toggle |
| `PREF_MAX_VOL` | `String` | Key for the user's configured maximum volume cap |
| `PREF_EXPORT` | `String` | Key for the "export session data" action preference |
| `PREF_CLEAR_DATA` | `String` | Key for the "clear all data" destructive action preference |

---

_No methods — this is a pure constants object._
