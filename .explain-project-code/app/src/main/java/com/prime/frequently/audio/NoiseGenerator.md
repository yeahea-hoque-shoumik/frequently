# NoiseGenerator.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.audio`

---

## Enum: `com.prime.frequently.audio.NoiseType`

**Summary**
Enumerates the four background noise modes available to `BinauralPlayer`: no noise, white, pink, and brown.

| Constant | Description |
|----------|-------------|
| `NONE` | No background noise |
| `WHITE` | Flat spectral density — equal energy per Hz |
| `PINK` | 1/f spectral density — equal energy per octave |
| `BROWN` | 1/f² spectral density — deep, low-frequency rumble |

---

## Class: `com.prime.frequently.audio.NoiseGenerator`

**Summary**
Generates per-sample background noise in three psychoacoustically distinct flavours: white (flat spectrum), pink (1/f — Voss-McCartney algorithm), and brown (1/f² — running integration). Each method returns one normalised float sample in [-1, 1].

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `random` | `Random` | Shared pseudo-random number source used by all three generators |
| `pinkRows` | `FloatArray(7)` | The 7 per-octave state values used by the Voss-McCartney pink noise algorithm |
| `pinkRunningSum` | `Float` | Running sum of all 7 row values; updated incrementally each sample |
| `pinkCounter` | `Int` | 15-bit counter whose bit transitions drive which rows are refreshed |
| `brownState` | `Float` | Current amplitude of the brown noise integrator; carries state across samples |

---

### `whiteNoiseSample`

**Function Name:** `com.prime.frequently.audio.NoiseGenerator.whiteNoiseSample`

**What** — Returns a single white noise sample uniformly distributed in [-1, 1].

**Why** — White noise has equal energy at every frequency; it sounds like static and is useful for masking environmental sounds.

**How**
1. Calls `random.nextFloat()` to get a value in [0, 1)
2. Scales and shifts to [-1, 1) by computing `* 2f - 1f`

**Insights**
- This is stateless — each call is independent with no carry-over state.

---

### `pinkNoiseSample`

**Function Name:** `com.prime.frequently.audio.NoiseGenerator.pinkNoiseSample`

**What** — Returns one sample of pink (1/f) noise using the Voss-McCartney algorithm with 7 octave rows.

**Why** — Pink noise has equal energy per octave (matching human auditory perception), making it more natural and less fatiguing than white noise for extended listening sessions.

**How**
1. Saves the previous counter value, then increments `pinkCounter` modulo 32767 (15-bit)
2. XORs old and new counter values to find which bits flipped
3. For each of the 7 bits that flipped: generates a new random value, updates `pinkRunningSum` by subtracting the old row value and adding the new one, stores the new value in the row
4. Generates one more white noise sample (`white`)
5. Returns `((pinkRunningSum + white) / 8f).coerceIn(-1f, 1f)`

**Insights**
- Dividing by 8 (7 rows + 1 white) normalises the sum to approximately ±1 range; `coerceIn` handles rare statistical outliers.
- The algorithm is extremely cheap: on average only 1–2 rows update per sample because low-order bits flip most frequently.
- Using XOR diff to find flipped bits is the standard Voss-McCartney optimisation; naively checking every bit every sample would have the same correctness but slightly worse performance.

---

### `brownNoiseSample`

**Function Name:** `com.prime.frequently.audio.NoiseGenerator.brownNoiseSample`

**What** — Returns one sample of brown (Brownian / red) noise with a 1/f² power spectrum.

**Why** — Brown noise is the most calming of the three types; its deep rumble resembles heavy rain or distant thunder and is used in sleep and deep-focus presets.

**How**
1. Generates a tiny random step: `random.nextFloat() * 0.02f - 0.01f` (range [-0.01, 0.01])
2. Adds the step to `brownState` and clamps to [-1, 1] with `coerceIn`
3. Returns the updated `brownState`

**Insights**
- The small step size (±0.01) produces a slowly wandering signal, which is what gives brown noise its characteristic low-frequency emphasis.
- The `coerceIn` clamp prevents the integrator from saturating at ±1 permanently — it reflects back toward zero when it hits the boundary.

---

### `reset`

**Function Name:** `com.prime.frequently.audio.NoiseGenerator.reset`

**What** — Resets all generator state to zero, preparing for a clean next playback session.

**Why** — Called by `BinauralPlayer` on stop/restart to ensure the pink noise rows and brown noise integrator don't carry state across sessions, which would cause an audible discontinuity at the start of the next play.

**How**
1. Fills `pinkRows` with zeros
2. Resets `pinkRunningSum` and `pinkCounter` to 0
3. Resets `brownState` to 0
