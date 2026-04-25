# FrequencyUtils.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.utils`

---

## Class: com.prime.frequently.utils.FrequencyUtils (object)

**Summary**
A stateless singleton utility object that provides two frequency-domain helpers: mapping a binaural beat frequency to its corresponding brain-wave category, and computing the per-ear carrier frequencies for stereo binaural playback.

**Instance Variables**

_No instance variables._

---

## Top-Level Functions

### `beatHzToCategory`

**Function Name:** `com.prime.frequently.utils.FrequencyUtils.beatHzToCategory`

**What** — Classifies a beat frequency in Hz into the appropriate `WaveCategory` brain-wave band and returns it.

**Why** — The brain-wave band boundaries (Delta < 4 Hz, Theta < 8 Hz, Alpha < 13 Hz, Beta < 30 Hz, Gamma ≥ 30 Hz) are the standard neuroscience classifications. Centralising this logic here prevents duplicated threshold checks across the app and keeps UI tinting, preset filtering, and session logging consistent.

**How**
1. Evaluates the `when` expression against the `beatHz` double value
2. Returns `DELTA` if `beatHz < 4.0`
3. Returns `THETA` if `beatHz < 8.0`
4. Returns `ALPHA` if `beatHz < 13.0`
5. Returns `BETA` if `beatHz < 30.0`
6. Returns `GAMMA` for all values ≥ 30.0 (the `else` branch)

**Insights**
- `SPIRITUAL` is not reachable via this function — it is a curated preset category, not a frequency-derived one
- Boundary values (exactly 4.0, 8.0, etc.) fall into the upper band (e.g., 4.0 → THETA, not DELTA) due to strict `<` comparisons — consistent with the standard "up to but not including" band definitions
- No range validation; negative beat frequencies would return `DELTA`, which is technically incorrect but unlikely to occur given upstream slider constraints

---

### `leftHz`

**Function Name:** `com.prime.frequently.utils.FrequencyUtils.leftHz`

**What** — Returns the carrier frequency for the left ear channel — which is simply the carrier frequency unchanged.

**Why** — Binaural beats are produced by playing slightly different frequencies in each ear. The left channel carries the base carrier tone; the brain perceives the beat as the difference between left and right.

**How**
1. Returns `carrierHz` directly — no computation

**Insights**
- The function exists for symmetry with `rightHz` and to make call-sites self-documenting (`leftHz(carrier, beat)` vs just using `carrier` directly)
- Convention choice: left = carrier, right = carrier + beat. The reverse (left = carrier + beat, right = carrier) would produce the same auditory effect

---

### `rightHz`

**Function Name:** `com.prime.frequently.utils.FrequencyUtils.rightHz`

**What** — Returns the carrier frequency for the right ear channel — the carrier plus the beat frequency offset.

**Why** — The binaural beat effect requires the two ears to receive tones that differ by exactly `beatHz`. Adding `beatHz` to the carrier for the right channel achieves this.

**How**
1. Returns `carrierHz + beatHz`

**Insights**
- For a 200 Hz carrier and a 10 Hz beat: left = 200 Hz, right = 210 Hz → the brain perceives a 10 Hz oscillation
- The formula is linear and exact — no rounding or clamping, preserving the precise beat frequency at the audio level

---
