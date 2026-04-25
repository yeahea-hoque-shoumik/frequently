# HomeViewModel.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.viewmodel`

---

## Sealed Class: com.prime.frequently.viewmodel.SessionEvent

**Summary**
One-shot UI events emitted via a `SharedFlow` to communicate terminal session transitions to the UI layer — events that should not persist in state and should not be missed by the observer.

**Instance Variables**

| Constant | Description |
|----------|-------------|
| `TimerCompleted` | Emitted when a timed session finishes naturally (countdown reaches zero) |
| `Stopped` | Emitted when the user manually stops a session before the timer completes |

---

## Enum Class: com.prime.frequently.viewmodel.TimerState

**Summary**
Models the four possible states of the session timer finite state machine.

| Value | Meaning |
|-------|---------|
| `IDLE` | No timer active; either duration is 0 or session has not started |
| `RUNNING` | Timer is counting down and audio is playing |
| `PAUSED` | Timer paused mid-countdown (audio also paused) |
| `COMPLETED` | Timer reached zero; session saved and audio faded out |

---

## Class: com.prime.frequently.viewmodel.HomeViewModel

**Summary**
The central activity-scoped ViewModel that owns all playback state. It manages the `BinauralPlayer` audio engine, all frequency and volume parameters, the countdown timer coroutine, and session persistence via `SessionRepository`. It exposes immutable `StateFlow`s for the UI and dispatches `SessionEvent`s as one-shot signals.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `player` | `BinauralPlayer` | The audio engine — manages `AudioTrack`, noise mixing, and fade in/out |
| `sessionRepo` | `SessionRepository` | Persists completed and stopped session records to Room |
| `_currentPresetName` | `MutableStateFlow<String>` | Name of the currently loaded preset; empty string for custom sessions |
| `currentPresetName` | `StateFlow<String>` | Public read-only view of the preset name |
| `_isPlaying` | `MutableStateFlow<Boolean>` | Internal playback state flag |
| `isPlaying` | `StateFlow<Boolean>` | Public read-only playback flag |
| `_carrierHz` | `MutableStateFlow<Double>` | Carrier (base) frequency in Hz; default 200.0 |
| `carrierHz` | `StateFlow<Double>` | Public read-only carrier frequency |
| `_beatHz` | `MutableStateFlow<Double>` | Binaural beat frequency in Hz; default 10.0 |
| `beatHz` | `StateFlow<Double>` | Public read-only beat frequency |
| `freqLeft` | `Double` (computed) | Left ear frequency derived from carrier (always equals carrier) |
| `freqRight` | `Double` (computed) | Right ear frequency derived from carrier + beat |
| `_volume` | `MutableStateFlow<Float>` | Master volume 0f–1f; default 0.5f |
| `volume` | `StateFlow<Float>` | Public read-only volume |
| `_timerState` | `MutableStateFlow<TimerState>` | Current timer FSM state |
| `timerState` | `StateFlow<TimerState>` | Public read-only timer state |
| `_durationSeconds` | `MutableStateFlow<Int>` | User-set session duration in seconds; 0 = unlimited |
| `durationSeconds` | `StateFlow<Int>` | Public read-only duration |
| `_remainingSeconds` | `MutableStateFlow<Int>` | Seconds remaining in the countdown |
| `remainingSeconds` | `StateFlow<Int>` | Public read-only remaining seconds |
| `remainingFormatted` | `String` (computed) | `remainingSeconds` formatted as `MM:SS` |
| `_elapsedSeconds` | `MutableStateFlow<Int>` | Seconds elapsed since play was pressed |
| `elapsedSeconds` | `StateFlow<Int>` | Public read-only elapsed seconds |
| `sessionStartTime` | `Long` | Epoch milliseconds when the current session started; 0 before first play |
| `_events` | `MutableSharedFlow<SessionEvent>` | Internal event bus for one-shot session events |
| `events` | `SharedFlow<SessionEvent>` | Public event stream |
| `timerJob` | `Job?` | Reference to the running countdown coroutine; cancelled on pause/stop |

---

### `play`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.play`

**What** — Starts audio playback and, if a duration is set, begins the countdown timer.

**Why** — Consolidates all start-of-session state transitions in one place: frequency push, volume set, elapsed reset, and optional timer kickoff.

**How**
1. Calls `player.setFrequencies(freqLeft, freqRight)` and `player.setVolume` to configure audio before starting
2. Calls `player.start()` to begin audio playback with a 500 ms fade-in
3. Sets `_isPlaying.value = true` and records `sessionStartTime`
4. Resets `_elapsedSeconds` to 0
5. If `_durationSeconds > 0`, sets `_remainingSeconds` to the full duration, transitions timer to `RUNNING`, and calls `launchTimerCoroutine()`

**Insights**
- Calling `setFrequencies` before `start` ensures the audio thread has the correct values from the first buffer
- `sessionStartTime` is used only as a fallback for unlimited sessions (where elapsed is computed from wall clock, not the coroutine counter)

---

### `pause`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.pause`

**What** — Pauses audio playback and transitions the timer from `RUNNING` to `PAUSED` if applicable.

**Why** — `pause()` and `resume()` are separate from `stop()` because pausing preserves session state (remaining time, elapsed time) while stop commits the session record.

**How**
1. Calls `player.pause()`
2. Sets `_isPlaying.value = false`
3. If timer was `RUNNING`, transitions to `PAUSED`

**Insights**
- The timer coroutine (`timerJob`) is not cancelled on pause — it keeps looping but skips the decrement when `timerState != RUNNING` (checked inside the loop)

---

### `resume`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.resume`

**What** — Resumes audio playback and re-activates the countdown timer if it was paused.

**Why** — Restores all session state exactly where it was paused, allowing mid-session interruptions without losing progress.

**How**
1. Calls `player.resume()`
2. Sets `_isPlaying.value = true`
3. If timer was `PAUSED`, transitions back to `RUNNING`

**Insights**
- The timer coroutine resumes naturally since it was never cancelled — it just starts decrementing again once `timerState == RUNNING`

---

### `togglePlayPause`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.togglePlayPause`

**What** — Dispatches to `play`, `pause`, or `resume` based on the current playback and timer state.

**Why** — The play/pause button in the UI has a single tap target; this function encodes the correct state machine transition so the UI does not need to know the internal states.

**How**
1. If `_isPlaying.value` is true → calls `pause()`
2. Else if `timerState == PAUSED` → calls `resume()` (resumes a paused session)
3. Else → calls `play()` (starts a fresh session)

**Insights**
- The ordering of the `when` branches matters: `isPlaying` is checked first, so a playing session is always paused on the first tap regardless of timer state

---

### `stop`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.stop`

**What** — Stops the session, saves a partial (incomplete) session record to the database, and emits `SessionEvent.Stopped`.

**Why** — User-initiated stop should still record the session so the history screen reflects actual usage. Sessions under 1 second are filtered out to avoid noise records.

**How**
1. Calls `buildSessionRecord(completed = false)` to capture current session state
2. Calls `player.fadeOutAndStop()` to gracefully end audio with a 500 ms fade
3. Sets `_isPlaying.value = false`
4. Cancels and resets the timer via `cancelTimer()`
5. In a `viewModelScope` coroutine: inserts the record only if `actualDurationSecs > 0`, then emits `SessionEvent.Stopped`

**Insights**
- The `record` is captured before stopping so it reflects the true elapsed time at the moment stop was tapped
- `actualDurationSecs > 0` guard prevents zero-duration records (e.g., stop tapped immediately after play)

---

### `setCarrierHz`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setCarrierHz`

**What** — Updates the carrier frequency and pushes the new stereo frequencies to the audio player if currently playing.

**Why** — Live frequency updates allow the user to adjust the sound while a session is in progress without stopping and restarting.

**How**
1. Updates `_carrierHz.value`
2. If `_isPlaying.value`, calls `player.setFrequencies(freqLeft, freqRight)`

**Insights**
- `freqLeft` and `freqRight` are computed properties that always read the latest `_carrierHz` and `_beatHz`, so no stale capture issue

---

### `setBeatHz`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setBeatHz`

**What** — Updates the beat frequency and pushes the new stereo frequencies to the audio player if currently playing.

**Why** — Same live-update rationale as `setCarrierHz`; the beat frequency is the primary therapeutic variable users adjust.

**How**
1. Updates `_beatHz.value`
2. If `_isPlaying.value`, calls `player.setFrequencies(freqLeft, freqRight)`

**Insights**
- Identical structure to `setCarrierHz`; the two could be merged into a single `setFrequencies(carrier, beat)` function if preferred

---

### `setVolume`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setVolume`

**What** — Updates the master volume state and immediately applies it to the audio player.

**Why** — Volume changes must take effect in real-time without restarting playback.

**How**
1. Updates `_volume.value`
2. Calls `player.setVolume(vol)`

**Insights**
- `player.setVolume` is thread-safe (reads an `AtomicFloat` or `volatile` field in `BinauralPlayer`)

---

### `setNoiseType`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setNoiseType`

**What** — Sets the background noise generator type on the player.

**Why** — Delegates noise configuration directly to `BinauralPlayer`, which owns the `NoiseGenerator` instance.

**How**
1. Assigns `type` to `player.noiseType`

---

### `setNoiseVolume`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setNoiseVolume`

**What** — Sets the volume of the background noise mix.

**Why** — Allows independent volume control for the binaural tone and the background noise.

**How**
1. Assigns `vol` to `player.noiseVolume`

---

### `applyPreset`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.applyPreset`

**What** — Applies all parameters from a `WavePreset` to the ViewModel and, if playing, immediately updates the audio engine.

**Why** — Selecting a preset in the Library should take effect instantly — both in the UI state and in the live audio stream if a session is active.

**How**
1. Updates `_carrierHz`, `_beatHz`, and `_currentPresetName` from the preset
2. Sets `player.noiseType` and `player.noiseVolume` from the preset's defaults
3. If `_isPlaying.value`, calls `player.setFrequencies(freqLeft, freqRight)` to apply the new frequencies live

**Insights**
- Does not call `setDurationMinutes` — preset application does not change the user's chosen session duration

---

### `setDurationMinutes`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setDurationMinutes`

**What** — Sets the session duration in minutes (converts to seconds internally).

**Why** — The timer picker UI works in minutes; the internal countdown operates in seconds.

**How**
1. Sets `_durationSeconds.value = minutes * 60`
2. Sets `_remainingSeconds.value = minutes * 60`

---

### `setDurationSeconds`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.setDurationSeconds`

**What** — Sets the session duration directly in seconds.

**Why** — Allows callers that already work in seconds (e.g., tests or presets with second-precision durations) to set the timer without conversion.

**How**
1. Sets both `_durationSeconds` and `_remainingSeconds` to `seconds`

---

### `clearDuration`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.clearDuration`

**What** — Resets the duration to 0 (unlimited) and cancels any active timer.

**Why** — Allows the user to switch to an unlimited session mid-flow without stopping playback.

**How**
1. Zeros `_durationSeconds` and `_remainingSeconds`
2. Calls `cancelTimer()` to cancel the coroutine and reset timer state to `IDLE`

---

### `launchTimerCoroutine`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.launchTimerCoroutine`

**What** — Starts the countdown coroutine that decrements `remainingSeconds` each second until zero, then triggers session completion.

**Why** — The coroutine approach is lightweight, cancellable, and integrates naturally with the ViewModel's lifecycle scope — no `Handler` or `CountDownTimer` needed.

**How**
1. Cancels any existing `timerJob` (guards against double-starts)
2. Launches a new coroutine on `viewModelScope`
3. Loops while `_remainingSeconds.value > 0`:
   - Delays 1000 ms
   - If `timerState == RUNNING` (not paused), decrements `remainingSeconds` and increments `elapsedSeconds`
4. After loop exits, if timer is not `IDLE`, calls `onTimerComplete()`

**Insights**
- The 1-second `delay` can drift slightly on a busy main thread; acceptable for a wellness timer but not for precision timing
- The `timerState` check inside the loop allows pause/resume without cancelling the Job

---

### `onTimerComplete`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.onTimerComplete`

**What** — Handles the natural end of a timed session: saves the session record, stops audio, and emits `TimerCompleted`.

**Why** — Separating timer completion from user-initiated stop allows different record-keeping semantics (`completed = true` vs `completed = false`) and different UI responses.

**How**
1. Captures the session record with `completed = true`
2. Calls `player.fadeOutAndStop()`
3. Sets `_isPlaying.value = false` and `_timerState` to `COMPLETED`
4. In a `viewModelScope` coroutine: inserts the record (always — no duration check needed since a timed session always has duration > 0) and emits `SessionEvent.TimerCompleted`

---

### `cancelTimer`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.cancelTimer`

**What** — Cancels the timer coroutine and resets all timer-related state to initial values.

**Why** — Called on stop and `clearDuration` to ensure the timer does not continue running after the session ends.

**How**
1. Cancels and nulls `timerJob`
2. Sets `_timerState` to `IDLE`
3. Resets `_remainingSeconds` to `_durationSeconds` (the configured duration, not zero)
4. Resets `_elapsedSeconds` to 0

**Insights**
- Resetting `_remainingSeconds` to `_durationSeconds` (not 0) means the progress bar resets to fully un-elapsed rather than fully elapsed, which is the correct visual state after a stop

---

### `buildSessionRecord`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.buildSessionRecord`

**What** — Constructs a `SessionRecord` from the current session state for persistence.

**Why** — Centralises record construction so both `stop()` and `onTimerComplete()` produce consistently structured records without duplicating field assignments.

**How**
1. Computes `actualDuration`:
   - If a duration was set: uses `_elapsedSeconds.value` (coroutine-tracked, accurate even when paused)
   - If unlimited session: computes from wall-clock delta `(now - sessionStartTime) / 1000`
2. Constructs and returns a `SessionRecord` with all current playback parameters, using `"Custom"` as the preset name if none was set

**Insights**
- The unlimited-session wall-clock fallback includes time spent paused, which slightly overstates actual listening time — a minor acceptable inaccuracy

---

### `onCleared`

**Function Name:** `com.prime.frequently.viewmodel.HomeViewModel.onCleared`

**What** — Stops the audio player when the ViewModel is destroyed.

**Why** — Ensures the `AudioTrack` is released and the background audio thread is stopped when the app is finished, preventing resource leaks.

**How**
1. Calls `super.onCleared()`
2. Calls `player.stop()` to release the `AudioTrack` and terminate the playback thread

**Insights**
- This is a critical cleanup call — `AudioTrack` is a system resource and must be released explicitly; failure to do so would hold the audio focus and potentially crash subsequent audio operations

---
