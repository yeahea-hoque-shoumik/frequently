# BinauralPlayer.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.audio`

---

## Class: `com.prime.frequently.audio.BinauralPlayer`

**Summary**
The real-time audio engine that drives binaural beat playback. It owns an `AudioTrack` in streaming mode, runs a dedicated MAX_PRIORITY audio thread, mixes sine-wave binaural output with optional background noise, and applies 500 ms envelope fades on start and stop to eliminate audible clicks.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `freqLeft` | `Double` | Carrier frequency for the left channel (Hz); written from the main thread, read on the audio thread |
| `freqRight` | `Double` | Carrier frequency for the right channel (Hz); the beat frequency is `freqRight - freqLeft` |
| `amplitude` | `Float` | Master output amplitude [0.0–1.0]; controlled by the volume slider |
| `noiseType` | `NoiseType` | Which background noise generator is active (WHITE, PINK, BROWN, or NONE) |
| `noiseVolume` | `Float` | Relative mix level of the background noise [0.0–1.0] |
| `audioTrack` | `AudioTrack?` | Android low-level PCM output handle; null when stopped |
| `playbackThread` | `Thread?` | Dedicated audio thread running `runLoop`; null when stopped |
| `isActive` | `Boolean` | Whether the audio loop should keep running; also used as the loop condition |
| `isPaused` | `Boolean` | Whether playback is temporarily paused (AudioTrack paused, loop spinning idle) |
| `fadingOut` | `Boolean` | Whether the envelope should ramp down; triggers graceful stop when it reaches zero |
| `envelope` | `Float` | Current gain of the amplitude envelope [0.0–1.0]; ramps up on start, down on fade-out |
| `fadeStep` | `Float` | Per-sample increment/decrement applied to `envelope` to achieve a 500 ms fade |
| `sineGen` | `SineWaveGenerator` | Generates the stereo binaural sine-wave PCM buffers |
| `noiseGen` | `NoiseGenerator` | Generates white, pink, or brown noise samples |

---

### `start`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.start`

**What** — Initialises the `AudioTrack`, starts the MAX_PRIORITY audio thread, and begins playback from silence (envelope = 0).

**Why** — Starting with `envelope = 0` guarantees a fade-in from silence, preventing the initial click that would occur if full-amplitude samples were written immediately.

**How**
1. Guards against double-start with `if (isActive) return`
2. Sets `isActive = true`, clears `isPaused` and `fadingOut`, resets `envelope` to 0
3. Calls `buildAudioTrack()` and calls `.play()` on the result
4. Creates a `Thread` running `::runLoop`, names it "BinauralPlayer", sets `MAX_PRIORITY`, and starts it

**Insights**
- `Thread.MAX_PRIORITY` reduces audio glitches (buffer underruns) by giving the audio thread scheduling precedence over UI work.

---

### `pause`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.pause`

**What** — Pauses audio output without releasing the `AudioTrack` or stopping the thread.

**Why** — A lightweight pause preserves the AudioTrack's internal state and phase continuity, allowing `resume()` to restart output instantly.

**How**
1. Guards: returns early if not active or already paused
2. Sets `isPaused = true`
3. Calls `audioTrack?.pause()` to halt PCM output immediately

**Insights**
- The audio thread continues looping but sleeps 20 ms per iteration while paused, keeping CPU usage negligible.

---

### `resume`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.resume`

**What** — Resumes playback after a pause, continuing from the same phase position.

**Why** — Calling `audioTrack.play()` after a pause is more efficient than tearing down and rebuilding the track.

**How**
1. Guards: returns early if not active or not currently paused
2. Sets `isPaused = false`
3. Calls `audioTrack?.play()` to resume output

---

### `stop`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.stop`

**What** — Immediately halts playback, joins the audio thread, and releases all native resources.

**Why** — Used for emergency stops and lifecycle events (app backgrounded, Activity destroyed) where a click artefact is acceptable in exchange for immediate teardown.

**How**
1. Clears `fadingOut` and sets `isActive = false` to signal the audio loop to exit
2. Calls `audioTrack?.stop()` to unblock any in-progress `write()` call on the audio thread
3. Joins `playbackThread` with a 1-second timeout; swallows `InterruptedException`
4. Nulls `playbackThread`
5. Calls `releaseTrack()` and `resetGenerators()`

**Insights**
- The `stop()` on the AudioTrack is needed because `write(WRITE_BLOCKING)` will block indefinitely until the driver accepts the buffer; stopping the track causes it to return immediately with an error code.

---

### `fadeOutAndStop`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.fadeOutAndStop`

**What** — Signals the audio thread to ramp the envelope to zero over 500 ms, then self-stop.

**Why** — Preferred over `stop()` for user-initiated stops because it eliminates the audible click of an abrupt cut.

**How**
1. Sets `fadingOut = true` if currently active — the audio thread handles the rest autonomously

**Insights**
- The audio thread owns the teardown in this path: once `envelope` reaches 0 it sets `isActive = false` and calls `releaseTrack()`, keeping all cleanup on the audio thread.

---

### `setFrequencies`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.setFrequencies`

**What** — Updates the left and right channel frequencies in a thread-safe manner.

**Why** — Both fields are `@Volatile`, so writing them from the main thread is immediately visible to the audio thread on the next buffer iteration without a lock.

**How**
1. Assigns `freqLeft = left` and `freqRight = right`

---

### `setVolume`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.setVolume`

**What** — Updates the master amplitude, clamping to the valid [0, 1] range.

**Why** — `coerceIn` prevents out-of-range values from causing clipping or distortion if a caller passes a value outside bounds.

**How**
1. Assigns `amplitude = vol.coerceIn(0f, 1f)`

---

### `runLoop`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.runLoop`

**What** — The core audio rendering loop that generates, mixes, and writes PCM buffers to the `AudioTrack` until playback stops.

**Why** — Running this on a dedicated MAX_PRIORITY thread decouples audio rendering from the UI thread, preventing glitches caused by UI jank or garbage collection pauses.

**How**
1. Allocates a `FloatArray` of `BUFFER_FRAMES * 2` (interleaved stereo)
2. Loops while `isActive`; if `isPaused`, sleeps 20 ms and continues
3. Snapshots all `@Volatile` parameters once per buffer to ensure they stay consistent across the buffer
4. Calls `sineGen.generateStereoBuffer(fl, fr, 1.0f, BUFFER_FRAMES)` to get normalised binaural PCM
5. For each frame: ramps `envelope` up or down by `fadeStep`; multiplies sine sample by `amp * envelope`; adds a noise sample scaled by `noiseVolume`; clamps to [-1, 1] and writes to the interleaved buffer
6. Calls `audioTrack?.write(buffer, 0, buffer.size, WRITE_BLOCKING)`; breaks on error
7. If `fadingOut && envelope <= 0`, sets `isActive = false` to exit the loop
8. After the loop: if in fade-out path, calls `releaseTrack()`; always calls `resetGenerators()`

**Insights**
- Snapshotting `@Volatile` fields into local variables per buffer (step 3) avoids the overhead of volatile reads on every sample and ensures the same parameter values are used for all frames in a buffer.
- `WRITE_BLOCKING` is the correct mode for a streaming AudioTrack — it back-pressures the audio thread to the hardware playback rate.
- The same noise sample is added to both left and right channels, which is intentional — background noise should not be spatialized.

---

### `releaseTrack`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.releaseTrack`

**What** — Stops and releases the `AudioTrack`, freeing its native audio hardware resources.

**Why** — Failing to call `release()` on an `AudioTrack` leaks a native audio session and can prevent other apps from acquiring the audio focus.

**How**
1. Returns early if `audioTrack` is null (already released)
2. Nulls `audioTrack` first to prevent double-release
3. Wraps `track.stop()` in `runCatching` (it may throw if already stopped)
4. Calls `track.release()`

---

### `resetGenerators`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.resetGenerators`

**What** — Resets the sine wave and noise generators to their initial state and zeroes the envelope.

**Why** — Resetting phase accumulators ensures the next `start()` begins cleanly from phase 0, preventing phase discontinuities that would sound like a click.

**How**
1. Calls `sineGen.reset()`
2. Calls `noiseGen.reset()`
3. Sets `envelope = 0f`

---

### `buildAudioTrack`

**Function Name:** `com.prime.frequently.audio.BinauralPlayer.buildAudioTrack`

**What** — Constructs and returns a configured `AudioTrack` for streaming 44100 Hz stereo float PCM.

**Why** — Using the Builder API ensures all required parameters are set and avoids the deprecated constructor overloads.

**How**
1. Queries `AudioTrack.getMinBufferSize()` for the minimum hardware buffer
2. Takes the max of the hardware minimum and `BUFFER_FRAMES * 2 * 4` bytes to ensure the buffer can hold a full render cycle
3. Builds `AudioAttributes` with `USAGE_MEDIA` / `CONTENT_TYPE_MUSIC`
4. Builds `AudioFormat` with 44100 Hz sample rate, stereo channel mask, and `ENCODING_PCM_FLOAT`
5. Sets transfer mode to `MODE_STREAM` (caller pushes data via `write()`)
6. Returns the constructed `AudioTrack`

**Insights**
- `ENCODING_PCM_FLOAT` provides 32-bit float samples, avoiding quantisation noise from integer PCM and making the amplitude math straightforward (no need to scale to ±32767).
- `MODE_STREAM` is required for continuous, low-latency audio generation.
