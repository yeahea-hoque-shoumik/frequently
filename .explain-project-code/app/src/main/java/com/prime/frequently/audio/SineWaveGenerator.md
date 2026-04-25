# SineWaveGenerator.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.audio`

---

## Class: `com.prime.frequently.audio.SineWaveGenerator`

**Summary**
Generates interleaved stereo PCM float buffers containing binaural sine waves. The left and right channels carry independently-tuned sine waves; the difference in their frequencies creates the binaural beat perceived by the listener. Phase is tracked continuously across successive buffer calls to prevent discontinuities.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `sampleRate` | `Int` | Samples per second (default 44100 Hz); determines the mapping between sample index and time |
| `phase` | `Double` | Accumulated time offset in seconds; carries phase state across successive `generateStereoBuffer` calls |

---

### `generateStereoBuffer`

**Function Name:** `com.prime.frequently.audio.SineWaveGenerator.generateStereoBuffer`

**What** — Fills and returns an interleaved stereo float buffer of `numFrames` frames, where the left channel is a sine at `freqLeft` Hz and the right is a sine at `freqRight` Hz.

**Why** — Binaural beats require two independent frequencies delivered to each ear separately. Tracking `phase` across calls ensures the sine waves remain continuous at buffer boundaries, which eliminates the clicks that would occur from phase resets.

**How**
1. Allocates `FloatArray(numFrames * 2)` for interleaved left/right samples
2. For each frame index `i`: computes `t = phase + i / sampleRate` (absolute time in seconds)
3. Writes `amplitude * sin(2π * freqLeft * t)` to even indices (left channel)
4. Writes `amplitude * sin(2π * freqRight * t)` to odd indices (right channel)
5. Advances `phase` by `numFrames / sampleRate` after the loop so the next call continues seamlessly
6. Returns the filled buffer

**Insights**
- Using `Double` for `phase` and time `t` is critical: at 44100 Hz a `Float` accumulator would lose sub-sample precision after a few minutes, causing audible pitch drift.
- The amplitude parameter is accepted here but `BinauralPlayer` always passes `1.0f` and applies its own envelope/amplitude multiplication, so this parameter is somewhat redundant in practice.
- Advancing `phase` after the loop (not before) means the first call starts at `t = 0`, which produces a clean zero-crossing start.

---

### `reset`

**Function Name:** `com.prime.frequently.audio.SineWaveGenerator.reset`

**What** — Resets the phase accumulator to zero.

**Why** — Called by `BinauralPlayer` after playback stops so the next session starts from a zero-phase sine wave, avoiding a phase-offset click at the beginning.

**How**
1. Sets `phase = 0.0`
