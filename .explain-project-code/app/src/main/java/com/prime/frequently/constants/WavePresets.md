# WavePresets.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.constants`

---

## Class: `com.prime.frequently.constants.WavePresets`

**Summary**
A singleton object that defines the complete built-in preset library — 18 `WavePreset` instances spanning five brain-wave bands (Delta, Theta, Alpha, Beta, Gamma) plus a Spiritual category. It is the single source of truth for all pre-configured frequency combinations, noise settings, and recommended durations.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `DEEP_SLEEP` | `WavePreset` | Delta 2 Hz beat, 150 Hz carrier, brown noise — for deep restorative sleep |
| `HEALING_REST` | `WavePreset` | Delta 1 Hz beat, 150 Hz carrier, brown noise — for cellular restoration |
| `DEEP_MEDITATION` | `WavePreset` | Theta 5 Hz beat, 200 Hz carrier — for deep meditative states |
| `CREATIVE_FLOW` | `WavePreset` | Theta 6 Hz beat, 200 Hz carrier — for creative insight |
| `INTUITION` | `WavePreset` | Theta 7 Hz beat, 200 Hz carrier — for heightened intuitive awareness |
| `CALM_AWARENESS` | `WavePreset` | Alpha 10 Hz beat, 200 Hz carrier — relaxed alert presence |
| `RELAXED_FOCUS` | `WavePreset` | Alpha 11 Hz beat, 200 Hz carrier — calm readiness for work |
| `DREAMY_STATE` | `WavePreset` | Alpha 8 Hz beat, 200 Hz carrier — gentle dream-like relaxation |
| `STUDY_MODE` | `WavePreset` | Beta 15 Hz beat, 300 Hz carrier, pink noise — sustained deep-work focus |
| `ACTIVE_THINKING` | `WavePreset` | Beta 20 Hz beat, 300 Hz carrier — sharp analytical thinking |
| `PEAK_PERFORMANCE` | `WavePreset` | Gamma 40 Hz beat, 400 Hz carrier — maximum cognitive performance |
| `INSIGHT` | `WavePreset` | Gamma 35 Hz beat, 400 Hz carrier — heightened perception |
| `PRE_SALAH_CALM` | `WavePreset` | Alpha 10 Hz, spiritual — settle the mind before prayer |
| `POST_SALAH_EXTENSION` | `WavePreset` | Theta 6 Hz, spiritual, pink noise — extend post-prayer khushu |
| `TAHAJJUD_PREP` | `WavePreset` | Theta 4 Hz, spiritual, brown noise — ease waking for night prayer |
| `QURAN_MEMORIZATION` | `WavePreset` | Theta 7 Hz, spiritual — receptive state for memorization |
| `RAMADAN_FOCUS` | `WavePreset` | Alpha 8 Hz, spiritual — fasting-adapted calm focus |
| `DHIKR_DEEPENING` | `WavePreset` | Theta 5 Hz, spiritual — deepen the state of remembrance |
| `ALL` | `List<WavePreset>` | Ordered list of all 18 presets; the canonical source consumed by `PresetRepository` |

---

_No methods — this is a pure data constants object._

**Insights**
- Spiritual presets include an `nameArabic` field, supporting bilingual display in the UI.
- The carrier Hz choices follow the band-appropriate ranges defined in `CLAUDE.md`: Delta/Theta at 150–200 Hz, Alpha/Beta at 200–300 Hz, Gamma at 400 Hz.
- `STUDY_MODE` and `POST_SALAH_EXTENSION` are the only presets with non-brown noise defaults, reflecting their different use-case profiles.
