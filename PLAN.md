# Android Binaural Beat App — Granular Build Plan

A step-by-step plan to build a binaural beat generator app in **Native Android (Kotlin)** with preset wave categories, custom Hz input, timer, session history, background noise mixer, and advanced features.

UX: user should be able to 

1. listen to binaural beats in a music player like interface
2. change different types of binaural beats, base hz, timer to listen, volume
3. check previous listen history
4. mixup a new kind of track by stacking multiple kind/hz of beats on sequential order. each sequence having distinct times if needed.
5. save all the created tracks on device.
> **Decision:** Building in Native Android (Kotlin + Android Studio)  for maximum audio performance, full platform API access, and tighter control over the `AudioTrack` low-level audio engine.
## Feature Priority Summary

| Priority | Feature | Why |
| --- | --- | --- |
| 🔴 Must Have | Progressive Frequency Transitions | Scientifically superior to all competitors |
| 🔴 Must Have | Wind Down Stack | Most-used sleep use case, fully automated |
| 🔴 Must Have | Session Intent → auto recommendation | Makes app beginner friendly instantly |
| 🔴 Must Have | Offline + Privacy-First | Trust differentiator, Play Store advantage |
| 🟡 High Value | Prayer-Aligned Presets | Unique audience, zero global competition |
| 🟡 High Value | Frequency Journey Builder | Power user feature, shareable |
| 🟡 High Value | Streak + Habit Tracker | Drives daily retention |
| 🟢 Nice to Have | Isochronic Mode | Expands beyond headphone users |
| 🟢 Nice to Have | Brain State Estimator | Engaging, personalized |
| 🟢 Nice to Have | Sensitivity Calibration | Deep personalization |

## Key Dependencies Summary

| Library | Purpose |
| --- | --- |
| `AudioTrack` (Android SDK) | Low-level PCM audio — no external lib needed |
| `androidx.room` | Local database for sessions and journeys |
| `androidx.lifecycle` (ViewModel + LiveData) | Architecture + reactive UI |
| `androidx.navigation` | Fragment navigation |
| `kotlinx.coroutines` | Async audio thread + timer |
| `MPAndroidChart` | Session history charts |
| `androidx.preference` | Settings screen |
| `AlarmManager` (Android SDK) | Daily reminder notifications |
| `Adhan` (optional) | Prayer time calculation |
| `Gson` or `kotlinx.serialization` | Journey JSON export/import |

> **Note:** No third-party audio library. The entire audio engine is built on Android's native `AudioTrack` API — giving maximum control, minimum latency, and smallest APK size.
> 

---

## Build Order Summary

```
Phase 0  → Environment Setup (Android Studio + Kotlin project)
Phase 1  → Audio Engine ← MOST CRITICAL (AudioTrack + SineWaveGenerator)
Phase 2  → Noise Mixer (NoiseGenerator + mixing in BinauralPlayer)
Phase 3  → Timer System (coroutine countdown + auto-stop)
Phase 4  → Preset Wave Categories (data model + RecyclerView UI)
Phase 5  → Custom Hz Input (sliders + live preview)
Phase 6  → Session History (Room DB + HistoryFragment)
Phase 7  → Main UI (HomeFragment + BottomNavigation)
Phase 8  → Settings (PreferenceFragment + headphone detection)
Phase 9  → Background Playback (ForegroundService + notification)
Phase 10 → Polish + Testing + Release APK
Phase 11 → Advanced Features:
           11.1 Session Intent System
           11.2 Progressive Transitions ← builds on AudioEngine
           11.3 Journey Builder (Room + drag-drop UI)
           11.4 Prayer-Aligned Presets
           11.5 Isochronic Mode
           11.6 Brain State Estimator
           11.7 Streak + Habit Tracker
           11.8 Wind Down Stack ← uses 11.2
           11.9 Sensitivity Calibration
           11.10 Privacy-First Polish
```

---

> **Critical Rule:** Build the audio engine first. Test it thoroughly on a real device with headphones before touching UI. A pixel-perfect UI with broken audio is worthless. `AudioTrack` is powerful but unforgiving — get Phase 1 solid before everything else.
> 

## Phase 0 — Environment Setup

### Step 0.1 — Install Android Studio

- Download Android Studio from [developer.android.com/studio](http://developer.android.com/studio)
- Install with default settings (includes Android SDK)
- Launch and complete the setup wizard
- Install Android SDK API level 33 (Android 13) as minimum target
- Install Android SDK API level 34 or 35 as compile target

### Step 0.2 — Configure Emulator

- Open AVD Manager in Android Studio
- Create new virtual device: Pixel 6, API 33
- Allocate at least 2GB RAM to emulator
- Enable hardware audio in emulator settings
- Run emulator and confirm it launches

### Step 0.3 — Enable Physical Device Testing

- Enable Developer Options on Android phone (tap Build Number 7 times)
- Enable USB Debugging
- Connect via USB and run `adb devices` to confirm detection
- Physical device strongly preferred for audio testing — emulator audio is unreliable

### Step 0.4 — Create New Android Project

- File → New → New Project → Empty Activity
- Name: `BinauralBeats`
- Package name: `com.yourname.binauralbeats`
- Language: **Kotlin**
- Minimum SDK: API 26 (Android 8.0) — covers 95%+ of active devices
- Build configuration: Kotlin DSL (build.gradle.kts)

### Step 0.5 — Set Up Project Structure

Create the following package structure under `app/src/main/java/com/yourname/binauralbeats/`:

```
com.yourname.binauralbeats/
  ui/
    MainActivity.kt
    HomeFragment.kt
    PresetsFragment.kt
    HistoryFragment.kt
    SettingsFragment.kt
    JourneyBuilderFragment.kt
  audio/
    AudioEngine.kt
    SineWaveGenerator.kt
    NoiseGenerator.kt
    BinauralPlayer.kt
  service/
    AudioForegroundService.kt
  data/
    SessionRecord.kt
    WavePreset.kt
    FrequencyJourney.kt
    FrequencyWaypoint.kt
    AppDatabase.kt
    SessionDao.kt
    JourneyDao.kt
  viewmodel/
    HomeViewModel.kt
    HistoryViewModel.kt
    PresetsViewModel.kt
  repository/
    SessionRepository.kt
    PresetRepository.kt
  constants/
    WavePresets.kt
    AppConstants.kt
  utils/
    FrequencyUtils.kt
    TimeUtils.kt
```

### Step 0.6 — Add Dependencies to build.gradle.kts

```kotlin
dependencies {
    // Architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Room (local database)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Prayer times (optional, for spiritual presets)
    implementation("com.batoulapps.adhan:adhan:1.2.1")
}
```

Run `Gradle Sync` after adding dependencies.

---

## Phase 1 — Audio Engine (Core)

This is the most critical phase. Everything else depends on it. Native Android gives direct access to `AudioTrack` — the lowest level audio API available on Android.

### Step 1.1 — Understand AudioTrack

`AudioTrack` is Android's low-level PCM audio API. It allows writing raw audio samples directly to the hardware audio buffer — essential for precise binaural beat generation.

Key parameters:

```kotlin
val sampleRate = 44100          // Hz — CD quality
val channelConfig = AudioFormat.CHANNEL_OUT_STEREO
val encoding = AudioFormat.ENCODING_PCM_FLOAT
val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, encoding)
```

### Step 1.2 — Understand Sine Wave Formula

Before writing code, internalize this:

```
sample = amplitude × sin(2π × frequency × t)
```

where `t` = current sample index / sampleRate

For stereo binaural:

```
leftSample  = amplitude × sin(2π × freqLeft  × t)
rightSample = amplitude × sin(2π × freqRight × t)
```

### Step 1.3 — Implement SineWaveGenerator.kt

```kotlin
class SineWaveGenerator(
    private val sampleRate: Int = 44100
) {
    private var phase = 0.0

    fun generateStereoBuffer(
        freqLeft: Double,
        freqRight: Double,
        amplitude: Float,
        numFrames: Int
    ): FloatArray {
        val buffer = FloatArray(numFrames * 2) // stereo: 2 samples per frame
        for (i in 0 until numFrames) {
            val t = phase + i.toDouble() / sampleRate
            val left  = (amplitude * Math.sin(2.0 * Math.PI * freqLeft  * t)).toFloat()
            val right = (amplitude * Math.sin(2.0 * Math.PI * freqRight * t)).toFloat()
            buffer[i * 2]     = left   // left channel
            buffer[i * 2 + 1] = right  // right channel
        }
        phase += numFrames.toDouble() / sampleRate
        return buffer
    }
}
```

### Step 1.4 — Implement BinauralPlayer.kt

- Create `AudioTrack` instance in `MODE_STREAM` mode
- Run audio generation loop on a dedicated background thread (NOT main thread)
- Continuously generate buffers and write to `AudioTrack`
- Handle thread lifecycle: start, pause, resume, stop

```kotlin
class BinauralPlayer {
    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null
    private var isPlaying = false
    var freqLeft: Double = 200.0
    var freqRight: Double = 210.0  // 10 Hz beat
    var amplitude: Float = 0.5f

    fun start() { /* create AudioTrack, start thread */ }
    fun pause() { /* pause thread, pause AudioTrack */ }
    fun resume() { /* resume thread */ }
    fun stop() { /* stop thread, release AudioTrack */ }
    fun setFrequencies(left: Double, right: Double) { /* update live */ }
    fun setVolume(vol: Float) { /* update amplitude */ }
}
```

### Step 1.5 — Implement Continuous Buffer Loop

- Generate buffer of ~4096 frames per iteration
- Write to `AudioTrack` with `write(buffer, 0, buffer.size, WRITE_BLOCKING)`
- Loop continuously while `isPlaying = true`
- Smaller buffer = lower latency but more CPU; 4096 frames is a good balance

### Step 1.6 — Implement Smooth Fade In / Fade Out

- Maintain an `envelopeAmplitude` value that ramps up/down
- Fade in: ramp from 0.0 to target amplitude over first 500ms (~22050 samples)
- Fade out: ramp from current amplitude to 0.0 over 500ms before stopping
- Multiply each sample by `envelopeAmplitude` during generation
- Prevents audio clicks on start/stop

### Step 1.7 — Implement Volume Control

- `audioTrack.setVolume(volume)` for overall volume (0.0 to 1.0)
- Also adjustable via amplitude multiplier in buffer generation
- Test: smooth volume change during playback with no artifacts

### Step 1.8 — Run Audio on Kotlin Coroutine / Thread

- Audio loop MUST run on a background thread — never `Dispatchers.Main`
- Use `Dispatchers.Default` coroutine or a dedicated `Thread`
- UI updates flow back through `LiveData` or `StateFlow` on main thread

---

## Phase 2 — Background Noise Mixer

### Step 2.1 — Understand Noise Types

- **White noise**: Equal energy at all frequencies — harsh, like static
- **Pink noise**: Energy decreases with frequency (1/f) — natural, like rain
- **Brown noise**: Steeper rolloff (1/f²) — deep, rumbling, most calming

### Step 2.2 — Implement NoiseGenerator.kt

Create a single class handling all noise types:

```kotlin
class NoiseGenerator {
    private val random = Random()

    fun whiteNoiseSample(): Float =
        (random.nextFloat() * 2f - 1f)

    // Pink noise: Voss-McCartney algorithm
    private val pinkState = FloatArray(7)
    fun pinkNoiseSample(): Float {
        // sum of 7 octave-scaled white noise generators
        // implementation detail: update one row per sample based on trailing zeros of counter
        // output = sum of all rows, normalized to -1..1
    }

    // Brown noise: running integration of white noise
    private var brownState = 0f
    fun brownNoiseSample(): Float {
        brownState = (brownState + (random.nextFloat() * 0.02f - 0.01f)).coerceIn(-1f, 1f)
        return brownState
    }
}
```

### Step 2.3 — Mix Binaural + Noise in BinauralPlayer

- Add `noiseType: NoiseType` and `noiseVolume: Float` parameters
- In buffer generation loop, mix per sample:

```kotlin
val noiseSample = when (noiseType) {
    NoiseType.WHITE  -> noiseGen.whiteNoiseSample()
    NoiseType.PINK   -> noiseGen.pinkNoiseSample()
    NoiseType.BROWN  -> noiseGen.brownNoiseSample()
    NoiseType.NONE   -> 0f
}
val mixed = (binauralSample * binauralVol) + (noiseSample * noiseVol)
buffer[i] = mixed.coerceIn(-1f, 1f) // clipping prevention
```

---

## Phase 3 — Timer System

### Step 3.1 — Implement Timer in HomeViewModel

- Use `viewModelScope.launch` with a coroutine
- `delay(1000)` each iteration, decrement `remainingSeconds`
- Expose as `StateFlow<Int>` to the UI

### Step 3.2 — Implement Auto-Stop

When `remainingSeconds` reaches 0:

- Call `binauralPlayer.fadeOutAndStop()`
- Save session record to Room database
- Emit completion event to UI (show dialog or notification)

### Step 3.3 — Timer Controls

- Play / Pause button
- Stop button with confirmation `AlertDialog`
- Preset duration chips: 5 / 10 / 20 / 30 / 45 / 60 / 90 min
- Custom duration: `NumberPicker` or `EditText` dialog

### Step 3.4 — Timer Display Widget

- Circular `ProgressBar` (custom drawn with `Canvas` or `CircularProgressIndicator`)
- Center text: MM:SS countdown
- Color transitions: green → yellow → orange using `ValueAnimator`

---

## Phase 4 — Preset Wave Categories

### Step 4.1 — Define WavePreset Data Class

```kotlin
data class WavePreset(
    val id: String,
    val name: String,
    val nameArabic: String = "",
    val category: WaveCategory,  // DELTA, THETA, ALPHA, BETA, GAMMA, SPIRITUAL
    val carrierHz: Double,
    val beatHz: Double,
    val description: String,
    val noiseType: NoiseType = NoiseType.NONE,
    val noiseVolume: Float = 0f,
    val recommendedDurationMin: Int = 20,
    val colorRes: Int
)
```

### Step 4.2 — Create Preset Library in WavePresets.kt

Define all presets as a constant list:

**Delta (sleep/healing):**

- Deep Sleep: carrier 150 Hz, beat 2 Hz
- Healing Rest: carrier 150 Hz, beat 1 Hz

**Theta (meditation/creativity):**

- Deep Meditation: carrier 200 Hz, beat 5 Hz
- Creative Flow: carrier 200 Hz, beat 6 Hz
- Intuition: carrier 200 Hz, beat 7 Hz

**Alpha (calm focus):**

- Calm Awareness: carrier 200 Hz, beat 10 Hz
- Relaxed Focus: carrier 200 Hz, beat 11 Hz
- Dreamy State: carrier 200 Hz, beat 8 Hz

**Beta (active focus):**

- Study Mode: carrier 300 Hz, beat 15 Hz
- Active Thinking: carrier 300 Hz, beat 20 Hz

**Gamma (peak cognition):**

- Peak Performance: carrier 400 Hz, beat 40 Hz
- Insight: carrier 400 Hz, beat 35 Hz

**Special:**

- Flow State Journey: progressive transition (see Phase 11.2)
- 40 Hz Special: carrier 400 Hz, beat 40 Hz

**Spiritual:**

- Pre-Salah Calm, Post-Salah Extension, Tahajjud Prep, Quran Memorization, Ramadan Focus, Dhikr Deepening (see Phase 11.4)

### Step 4.3 — Build Preset Selection UI

- `TabLayout` + `ViewPager2` for wave categories
- Each preset as a `CardView` in a `RecyclerView`
- Card shows: name, beat Hz badge, description, category color
- Tap card → apply preset to `HomeViewModel` → updates player

---

## Phase 5 — Custom Hz Input

### Step 5.1 — Build Custom Input UI in HomeFragment

- Two `EditText` fields: Carrier Hz and Beat Hz
- OR two sliders with text labels (more user friendly)
- Input validation: carrier 50–1000 Hz, beat 0.5–100 Hz
- Show computed left/right Hz in real time below the inputs

### Step 5.2 — Frequency Sliders

- `Slider` (Material) for carrier: range 100–500 Hz
- `Slider` for beat: range 0.5–100 Hz (use step 0.5)
- Sliders and text inputs stay in sync via two-way data binding
- Color indicator strip showing current wave band

### Step 5.3 — Live Frequency Preview Card

While adjusting show a live card:

- Left channel Hz
- Right channel Hz
- Beat frequency Hz
- Wave category label (Delta / Theta / Alpha / Beta / Gamma)
- Recommended use case text
- Update in real time with no debounce lag

---

## Phase 6 — Session History

### Step 6.1 — Define SessionRecord Entity (Room)

```kotlin
@Entity(tableName = "sessions")
data class SessionRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val startTime: Long,           // epoch millis
    val plannedDurationSecs: Int,
    val actualDurationSecs: Int,
    val presetName: String,        // "Custom" if no preset
    val carrierHz: Double,
    val beatHz: Double,
    val noiseType: String,
    val noiseVolume: Float,
    val completed: Boolean,
    val intentName: String = ""    // session intent if selected
)
```

### Step 6.2 — Set Up Room Database

- Create `AppDatabase.kt` extending `RoomDatabase`
- Define `SessionDao` with queries: `insert`, `getAll`, `deleteById`, `deleteAll`
- Use `Flow<List<SessionRecord>>` return type for reactive UI updates
- Initialize database with `Room.databaseBuilder` as singleton

### Step 6.3 — Auto-Save on Session End

- On timer completion: save with `completed = true`, `actualDuration = plannedDuration`
- On manual stop: save with `completed = false`, `actualDuration = elapsed`
- On app killed during session: save in `onDestroy` of foreground service

### Step 6.4 — Build History Screen (HistoryFragment)

- `RecyclerView` with `ListAdapter` + `DiffUtil` for efficient updates
- Each item: date/time, preset name, beat Hz + wave category, duration bar, ✓ or partial indicator
- Swipe left to delete (using `ItemTouchHelper`)
- Toolbar menu: Clear All (with `AlertDialog` confirmation)

### Step 6.5 — Session Statistics

- Total sessions, total minutes, current streak, longest streak
- Most used preset, favourite wave category
- `MPAndroidChart` bar chart: sessions per day for last 14 days
- `MPAndroidChart` pie chart: time distribution by wave category

---

## Phase 7 — Main UI / Home Screen

### Step 7.1 — Design Home Screen Layout (HomeFragment)

Use `ConstraintLayout` as root:

- Top bar: app name + settings `ImageButton`
- Frequency display: three large `TextView`s — Left Hz | Beat Hz | Right Hz
- Wave category `Chip` below frequency display (color coded)
- Horizontal `RecyclerView` for quick preset selection
- Large circular play/pause `FloatingActionButton`
- Circular timer `ProgressIndicator` around FAB
- Expandable `BottomSheet` for noise mixer panel

### Step 7.2 — Frequency Display Widget

- Three `TextView`s with large bold font (use `MaterialTextView`)
- Animate value changes with `ValueAnimator` (number count-up/down)
- Color coded by wave band via `ColorStateList`

### Step 7.3 — Play Controls

- `FloatingActionButton` — play/pause icon toggle
- Stop `ImageButton` (smaller, below FAB)
- Duration `ChipGroup` below stop button

### Step 7.4 — Noise Mixer BottomSheet

- `BottomSheetDialogFragment` or `BottomSheetBehavior` on a `ConstraintLayout`
- `ToggleButton` group: White / Pink / Brown / Off
- `Slider` for noise volume
- Real-time audio update on slider change

### Step 7.5 — Bottom Navigation

Use `BottomNavigationView` with `NavController`:

- Home (generator)
- Presets
- History
- Settings

---

## Phase 8 — Settings Screen

### Step 8.1 — Use PreferenceFragmentCompat

Native Android has first-class `Preferences` API — use it:

- `SwitchPreference`: Keep screen on during session
- `SwitchPreference`: Headphone warning
- `ListPreference`: Default noise type
- `ListPreference`: Audio sample rate (44100 / 48000 Hz)
- `ListPreference`: Theme (Light / Dark / System)
- `SeekBarPreference`: Default carrier Hz
- `Preference`: Redo calibration

### Step 8.2 — Headphone Detection

```kotlin
val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
val isHeadphonesConnected = audioManager.isWiredHeadsetOn ||
    audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        .any { it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
               it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
```

- Show `Snackbar` warning if playing without headphones
- Register `BroadcastReceiver` for `AudioManager.ACTION_HEADSET_PLUG`

---

## Phase 9 — Background Playback (Foreground Service)

### Step 9.1 — Create AudioForegroundService.kt

- Extend `Service`
- `BinauralPlayer` runs inside the service — NOT in Activity
- Activity binds to service via `ServiceConnection`
- Service continues running when Activity goes to background

### Step 9.2 — Add Permissions to AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<service
    android:name=".service.AudioForegroundService"
    android:foregroundServiceType="mediaPlayback" />
```

### Step 9.3 — Build Persistent Notification

```kotlin
val notification = NotificationCompat.Builder(this, CHANNEL_ID)
    .setContentTitle("Binaural Beats")
    .setContentText("$presetName • $beatHz Hz • $timeRemaining")
    .setSmallIcon(R.drawable.ic_headphones)
    .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
    .setOngoing(true)
    .build()
startForeground(NOTIFICATION_ID, notification)
```

### Step 9.4 — Handle Audio Interruptions

Register `AudioManager.OnAudioFocusChangeListener`:

```kotlin
override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
        AudioManager.AUDIOFOCUS_LOSS -> binauralPlayer.stop()
        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> binauralPlayer.pause()
        AudioManager.AUDIOFOCUS_GAIN -> binauralPlayer.resume()
    }
}
```

- Headphones unplugged → `BroadcastReceiver` for `ACTION_HEADSET_PLUG` → pause immediately
- Phone call received → audio focus loss → auto-pause

---

## Phase 10 — Polish and Testing

### Step 10.1 — Audio Quality Testing

- Test ONLY on real device with headphones (emulator audio unreliable)
- Verify true stereo separation: left and right channels are different
- Listen for clicks, dropouts, or buffer underruns
- Test all noise types at various volumes and mix levels
- Run a 90-minute session and monitor memory with Android Profiler

### Step 10.2 — UI/UX Testing

- Test all preset cards load and apply correctly
- Test custom frequency edge cases (min/max values, invalid input)
- Test timer: start, pause, resume, stop, complete
- Test session save/load/delete in history
- Test settings persistence after app restart and phone reboot
- Test dark mode and light mode
- Test font scaling (accessibility)

### Step 10.3 — Performance Profiling

- Open Android Studio Profiler
- Confirm audio thread stays below 5% CPU usage
- Confirm UI thread stays at 60fps (no jank)
- Confirm memory is stable over long sessions (no leaks)
- Use `StrictMode` in debug builds to catch accidental main-thread I/O

### Step 10.4 — Build Release APK

```bash
# In Android Studio: Build → Generate Signed Bundle/APK
# OR via command line:
./gradlew assembleRelease
```

- Sign with a keystore (create one in Android Studio)
- Enable ProGuard / R8 minification for smaller APK
- Target APK size under 10MB (native Android is lean)
- Test release APK on physical device before distribution

---

## Phase 11 — Advanced Features (Differentiators)

These features separate this app from every existing competitor. Implement after Phase 10.

---

### Feature 1 — Session Intent System (🔴 Must Have)

#### Step 11.1.1 — Define Intent Enum

```kotlin
enum class SessionIntent(val labelRes: Int, val descRes: Int) {
    DEEP_SLEEP(R.string.intent_deep_sleep, R.string.intent_deep_sleep_desc),
    MEDITATION(R.string.intent_meditation, R.string.intent_meditation_desc),
    STUDY_DEEP_WORK(R.string.intent_study, R.string.intent_study_desc),
    CREATIVE_THINKING(R.string.intent_creative, R.string.intent_creative_desc),
    FLOW_STATE(R.string.intent_flow, R.string.intent_flow_desc),
    ANXIETY_RELIEF(R.string.intent_anxiety, R.string.intent_anxiety_desc),
    PRAYER_PREP(R.string.intent_prayer_prep, R.string.intent_prayer_prep_desc),
    POST_PRAYER(R.string.intent_post_prayer, R.string.intent_post_prayer_desc),
}
```

#### Step 11.1.2 — Build Intent Recommendation Engine

Map each intent to optimal settings in `IntentRecommendationEngine.kt`:

- `DEEP_SLEEP` → carrier 150 Hz, beat 2 Hz, brown noise 60%, 45 min
- `FLOW_STATE` → progressive journey: beta 18 Hz → alpha 10 Hz → theta 6 Hz
- `STUDY_DEEP_WORK` → carrier 300 Hz, beat 15 Hz, pink noise 30%, 90 min
- `PRAYER_PREP` → carrier 200 Hz, beat 10 Hz, no noise, 5 min
- `ANXIETY_RELIEF` → carrier 200 Hz, beat 8 Hz alpha, brown noise 20%, 20 min

#### Step 11.1.3 — Build Intent Selection Screen

- `BottomSheetDialogFragment` or dedicated `Fragment`
- Grid of `CardView`s with icon + name + one-line description
- After selection: show recommended settings summary
- Accept button applies settings to `HomeViewModel`
- User can still fine-tune before pressing play

---

### Feature 2 — Progressive Frequency Transitions (🔴 Must Have)

#### Step 11.2.1 — Define Data Classes

```kotlin
data class FrequencyWaypoint(
    val atMinute: Int,
    val carrierHz: Double,
    val beatHz: Double,
    val transitionDurationSecs: Int = 60
)

data class FrequencyJourney(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val waypoints: List<FrequencyWaypoint>,
    val noiseType: NoiseType = NoiseType.NONE,
    val noiseVolume: Float = 0f
)
```

#### Step 11.2.2 — Implement Smooth Interpolation in AudioEngine

- `TransitionController` runs alongside the playback loop
- Every buffer callback: compute current target Hz based on elapsed time
- Formula: `currentHz = startHz + (endHz - startHz) × (elapsed / transitionDuration)`
- Pass updated Hz values to `SineWaveGenerator` for next buffer
- Transition is imperceptible — no clicks, no jumps

#### Step 11.2.3 — Built-In Journey Presets

```
Flow State Journey (75 min):
  0–10 min:  Beta 18 Hz   (struggle/loading)
  10–20 min: Alpha 10 Hz  (release — 5 min transition)
  20–75 min: Theta 6 Hz   (deep flow — 5 min transition)

Sleep Wind-Down (40 min):
  0–10 min:  Alpha 10 Hz + pink noise 30%
  10–20 min: Theta 6 Hz  + brown noise 50%
  20–40 min: Delta 2 Hz  + brown noise 70%
  Auto-stop

Deep Meditation (45 min):
  0–5 min:   Alpha 10 Hz
  5–15 min:  Theta 6 Hz
  15–45 min: Theta 5 Hz

Study Session (90 min):
  0–5 min:   Alpha 10 Hz (calm entry)
  5–90 min:  Beta 15 Hz  (sustained focus)
```

#### Step 11.2.4 — Journey Timeline UI

- Custom `View` drawn with `Canvas` API
- Horizontal timeline bar with waypoint markers
- Animated position indicator moves left → right as session progresses
- Tap a waypoint to see its Hz values in a tooltip
- Color coded segments by wave band

---

### Feature 3 — Frequency Journey Builder (🟡 High Value)

#### Step 11.3.1 — Build Journey Editor Fragment

- `RecyclerView` list of waypoints
- FAB to add new waypoint → `DialogFragment` to set: time (min), carrier Hz, beat Hz, transition duration
- Swipe to delete waypoint (`ItemTouchHelper`)
- Drag to reorder waypoints (`ItemTouchHelper` with drag handle)
- Preview button: animate through the journey on the timeline widget

#### Step 11.3.2 — Persist Custom Journeys in Room

```kotlin
@Entity(tableName = "journeys")
data class FrequencyJourneyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val waypointsJson: String,  // serialized List<FrequencyWaypoint> as JSON
    val noiseType: String,
    val noiseVolume: Float,
    val createdAt: Long
)
```

#### Step 11.3.3 — Journey Export/Import

- Export: serialize journey to JSON → share via Android `ShareCompat`
- Import: accept JSON file via `Intent.ACTION_GET_CONTENT` → parse → save
- Shareable text code format: `JOURNEY:150:2@0|200:10@10|200:6@20`
- QR code generation (optional): encode journey string as QR

---

### Feature 4 — Prayer-Aligned Presets (🟡 High Value — Unique)

No other app in the world has this. Serves 1.8 billion potential users.

#### Step 11.4.1 — Islamic Wellness Preset Category

| Preset Name | Carrier | Beat | Noise | Duration | Purpose |
| --- | --- | --- | --- | --- | --- |
| Pre-Salah Calm | 200 Hz | 10 Hz | None | 5 min | Settle mind before prayer |
| Post-Salah Extension | 200 Hz | 6 Hz theta | Pink 20% | 10 min | Extend khushu state |
| Tahajjud Prep | 150 Hz | 4 Hz | Brown 40% | 15 min | Ease waking at night |
| Quran Memorization | 200 Hz | 7 Hz theta | None | 30 min | Receptive learning state |
| Ramadan Focus | 200 Hz | 8 Hz alpha | None | 20 min | Fasting-adapted calm focus |
| Dhikr Deepening | 200 Hz | 5 Hz theta | None | 15 min | Deepen remembrance state |

#### Step 11.4.2 — Arabic Labels

- Show Arabic name below English name for each spiritual preset using `android:textDirection="rtl"`
- Example: "Pre-Salah Calm — تهيئة قبل الصلاة"

#### Step 11.4.3 — Prayer Time Awareness (Optional)

- Use `Adhan` library for local prayer time calculation (no internet needed)
- Device location from `FusedLocationProviderClient`
- Suggest relevant preset based on current prayer time proximity
- e.g. 10 minutes before Fajr → suggest "Tahajjud Prep"
- Surface as a home screen suggestion card

---

### Feature 5 — Isochronic Tone Mode (🟢 Nice to Have)

#### Step 11.5.1 — Implement Isochronic Generator

```kotlin
fun isochronicSample(carrierHz: Double, beatHz: Double, t: Double): Float {
    val carrier = Math.sin(2.0 * Math.PI * carrierHz * t)
    val pulse = if ((t * beatHz) % 1.0 < 0.5) 1.0 else 0.0
    val smoothPulse = applyEdgeFade(pulse, t, beatHz) // 5ms fade each edge
    return (carrier * smoothPulse).toFloat()
}
```

- Works without headphones — output same signal to both channels
- Does NOT require stereo separation

#### Step 11.5.2 — Mode Toggle

- Add to Settings: `ListPreference` — Binaural / Isochronic / Both
- In Isochronic mode: single Hz input (carrier + beat only)
- Show headphone warning only in Binaural and Both modes

---

### Feature 6 — Brain State Estimator (🟢 Nice to Have)

#### Step 11.6.1 — Collect State Inputs

Optional quick survey on session start via `BottomSheetDialogFragment`:

- Hours since waking (0–16 slider)
- Hours since last meal (0–8 slider)
- Subjective energy level (1–5 emoji scale)

#### Step 11.6.2 — Rule-Based State Estimation

```kotlin
fun estimateState(hoursSinceWake: Int, hoursSinceMeal: Int, energy: Int): BrainState {
    return when {
        hoursSinceWake <= 1 -> BrainState.THETA_LOW_ALPHA
        hoursSinceWake in 2..4 && hoursSinceMeal > 1 -> BrainState.ALPHA_LOW_BETA
        hoursSinceMeal in 1..2 -> BrainState.LOW_ALPHA_DROWSY
        hoursSinceWake > 10 -> BrainState.TIRED_HIGH_BETA
        energy >= 4 -> BrainState.ACTIVE_BETA
        else -> BrainState.ALPHA
    }
}
```

#### Step 11.6.3 — Display Recommendation Card

- `CardView` on home screen showing estimated state + recommendation
- Tap card → auto-apply recommended preset
- Dismissable if user doesn't want it

---

### Feature 7 — Streak and Habit Tracker (🟡 High Value)

#### Step 11.7.1 — Streak Logic in SessionRepository

- Query sessions by date using Room
- A day counts if `actualDurationSecs >= 300` (5 min minimum)
- Calculate current streak: count consecutive days backward from today
- Store `longestStreak` in `SharedPreferences`

#### Step 11.7.2 — Stats Dashboard

Add a Stats section to HistoryFragment:

- 🔥 Current streak + longest streak
- Total sessions and total minutes
- `MPAndroidChart` BarChart: sessions per day (last 14 days)
- `MPAndroidChart` PieChart: time split by wave category

#### Step 11.7.3 — Daily Reminder Notification

- `AlarmManager` with `setExactAndAllowWhileIdle` for reliable delivery
- User sets preferred reminder time in Settings
- Notification: "Time for your session 🎧"
- Respect `NotificationManager.areNotificationsEnabled()`
- Request `POST_NOTIFICATIONS` permission at runtime (Android 13+)

---

### Feature 8 — Wind Down Stack (🔴 Must Have)

One-tap automated pre-sleep neurological sequence.

#### Step 11.8.1 — Implement as Built-In Journey

Uses the Progressive Transition engine from Feature 2:

```
Phase 1 — 10 min: Alpha 10 Hz + pink noise 30%
        ↓ smooth 3-min transition
Phase 2 — 10 min: Theta 6 Hz + brown noise 50%
        ↓ smooth 3-min transition
Phase 3 — 20 min: Delta 2 Hz + brown noise 70%
        ↓ smooth 2-min fade out
Auto-stop
```

#### Step 11.8.2 — Sleep Mode UI

- Dedicated `SleepModeActivity` or fullscreen `Fragment`
- Pure black background (`#000000`)
- Minimal UI: moon icon, time remaining, stop button only
- Set `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` but dim to minimum brightness
- Override back press to show confirmation dialog ("Are you sure you want to stop?")

---

### Feature 9 — Sensitivity Calibration (🟢 Nice to Have)

#### Step 11.9.1 — Calibration Onboarding Flow

- `ViewPager2` onboarding shown once at first launch
- Step 1: Explanation screen — what binaural beats are, why calibration helps
- Step 2: Play 10 Hz alpha for 90 seconds with a progress bar
- Step 3: Three-button response: "Too drowsy" / "Just right" / "Too stimulating"
- Step 4: Summary — show adjusted personal baseline

#### Step 11.9.2 — Store and Apply Personal Baseline

```kotlin
// SharedPreferences
prefs.putFloat("personalAlphaPeak", 10f)  // adjusted by calibration

// In recommendation engine:
val adjusted = baseRecommendation + (personalAlphaPeak - 10f)
```

- User can redo calibration from Settings → "Recalibrate"

---

### Feature 10 — Offline-First, Privacy-First Design (🔴 Must Have)

#### Step 11.10.1 — Zero Network Requirements

- Remove all internet permission from `AndroidManifest.xml` entirely
- No `uses-permission android:name="android.permission.INTERNET"`
- This is visible to users on Play Store — a strong trust signal
- No analytics SDKs (no Firebase Analytics, no Crashlytics unless self-hosted)
- No ads SDK

#### Step 11.10.2 — All Data Local

- Room database on device storage only
- `SharedPreferences` for settings — device only
- Export: user-initiated JSON export via `Storage Access Framework`
- Import: user-initiated JSON import

#### Step 11.10.3 — Play Store Listing Trust Language

Include in description:

> "No account required. No internet permission. No ads. No tracking. All your data stays on your device."
> 

These four statements are verifiable by any user — they create deep trust.
