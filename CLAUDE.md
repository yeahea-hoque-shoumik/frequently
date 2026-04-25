# CLAUDE.md — Frequently (Binaural Beat App)

## Project Overview

**App name:** Frequently
**Package:** `com.prime.frequently`
**Language:** Kotlin (Native Android)
**Purpose:** Binaural beat generator with a music-player UI, preset wave categories, custom Hz input, session timer, history tracking, background noise mixer, and advanced neurological journey features.

Full specification: `docs/DevelopmentPlan.md`

---

## Git Rules (Absolute)

- **Never commit or push without explicit user confirmation.**
- **Force push (`git push --force` / `-f`) is permanently forbidden — no exceptions, even if instructed.**

---

## Build Configuration

| Field | Value |
|---|---|
| AGP | 9.1.1 |
| Compile SDK | 36 (release, minorApiLevel = 1) |
| Min SDK | 33 (Android 13) |
| Target SDK | 36 |
| Java compatibility | VERSION_11 |
| Build config language | Kotlin DSL (`build.gradle.kts`) |
| Version catalog | `gradle/libs.versions.toml` |

### Build Configuration Notes
- Kotlin plugin: `2.1.20` — declared in root `build.gradle.kts` (`apply false`), applied in `app/build.gradle.kts`
- KSP `2.1.20-1.0.32` replaces `kapt` for Room compiler (kapt is deprecated in Kotlin 2.x)
- JitPack added to `settings.gradle.kts` for MPAndroidChart
- `kotlinOptions { jvmTarget }` is NOT used — AGP 9.x propagates `compileOptions` SOURCE/TARGET_COMPATIBILITY to Kotlin automatically
- `android:colorBackground` (Material 2) removed from `themes.xml` — use `android:colorBackground` (android namespace) only
- `android:gap` is NOT a valid `LinearLayout` attribute — removed from all layouts; use explicit margins instead

---

## Architecture

Pattern: **MVVM** (ViewModel + LiveData/StateFlow + Repository)

```
com.prime.frequently/
  ui/
    MainActivity.kt          ← single Activity host, BottomNavigation
    HomeFragment.kt          ← player UI, frequency display, timer
    PresetsFragment.kt       ← preset category tabs + cards
    HistoryFragment.kt       ← session list + stats + charts
    SettingsFragment.kt      ← PreferenceFragmentCompat
    JourneyBuilderFragment.kt← drag-drop journey editor
  audio/
    AudioEngine.kt
    SineWaveGenerator.kt     ← stereo PCM sine wave generation
    NoiseGenerator.kt        ← white / pink / brown noise
    BinauralPlayer.kt        ← AudioTrack wrapper, thread lifecycle
  service/
    AudioForegroundService.kt← background playback + notification
  data/
    SessionRecord.kt         ← Room entity
    WavePreset.kt            ← preset data class
    FrequencyJourney.kt      ← journey + waypoint data classes
    FrequencyWaypoint.kt
    AppDatabase.kt           ← RoomDatabase singleton
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
    WavePresets.kt           ← all built-in preset definitions
    AppConstants.kt
  utils/
    FrequencyUtils.kt
    TimeUtils.kt
```

---

## Key Dependencies (Target)

| Library | Version | Purpose |
|---|---|---|
| `AudioTrack` (SDK) | — | Low-level PCM audio, no external lib |
| `androidx.room` | 2.6.1 | Local DB — sessions + journeys |
| `androidx.lifecycle` (VM + LiveData) | 2.7.0 | MVVM architecture |
| `androidx.navigation` | 2.7.6 | Fragment navigation |
| `kotlinx.coroutines` | 1.7.3 | Async audio thread + timer |
| `MPAndroidChart` | 3.1.0 | History charts |
| `androidx.preference` | — | Settings screen |
| `AlarmManager` (SDK) | — | Daily reminder notifications |
| `Adhan` | 1.2.1 | Prayer time calculation (optional) |
| `kotlinx.serialization` | — | Journey JSON export/import |

**No Flutter. No third-party audio library. No internet permission.**

---

## Audio Engine Rules

- All audio generation runs on a **background thread** (`Dispatchers.Default` or dedicated `Thread`) — never `Dispatchers.Main`.
- Sample rate: **44100 Hz**, stereo, `ENCODING_PCM_FLOAT`.
- Buffer size: **~4096 frames** per iteration.
- Phase must be tracked continuously across buffers to prevent phase discontinuity.
- Always apply **fade in / fade out (500 ms)** on start/stop to prevent clicks.
- Stereo binaural formula:
  - `left  = amplitude × sin(2π × freqLeft  × t)`
  - `right = amplitude × sin(2π × freqRight × t)`
  - `t = phase + i / sampleRate`

---

## Build Phases & Status

| Phase | Description | Status |
|---|---|---|
| 0 | Environment setup, project scaffold | ✅ Done |
| 1 | Audio engine (AudioTrack + SineWaveGenerator + BinauralPlayer) | ✅ Done |
| 2 | Background noise mixer (NoiseGenerator + mixing) | ✅ Done |
| 3 | Timer system (coroutine countdown + auto-stop) | ✅ Done |
| 4 | Preset wave categories (data model + RecyclerView UI) | ✅ Done |
| 5 | Custom Hz input (sliders + live preview) | ✅ Done |
| 6 | Session history (Room DB + HistoryFragment) | ✅ Done |
| 7 | Main UI (HomeFragment + BottomNavigation) | ✅ Done |
| 8 | Settings (PreferenceFragment + headphone detection) | ✅ Done |
| 9 | Background playback (ForegroundService + notification) | ✅ Done |
| 10 | Polish + testing + release APK | ⬜ Not started |
| 11.1 | Session Intent System | ✅ Done — SessionIntent enum, IntentRecommendationEngine, SessionIntentBottomSheet (tap ⋯ in Player) |
| 11.2 | Progressive Frequency Transitions | ✅ Done — AudioEngine interpolation, JourneyPresets (Flow State / Wind Down / Deep Meditation / Study Session), journey tick coroutine in HomeViewModel |
| 11.3 | Journey Builder | ⬜ Not started |
| 11.4 | Prayer-Aligned Presets | ⬜ Not started |
| 11.5 | Isochronic Tone Mode | ⬜ Not started |
| 11.6 | Brain State Estimator | ⬜ Not started |
| 11.7 | Streak + Habit Tracker | ⬜ Not started |
| 11.8 | Wind Down Stack | ✅ Done — built-in journey preset in Library (Journey tab) + surfaced via WIND_DOWN intent |
| 11.9 | Sensitivity Calibration | ⬜ Not started |
| 11.10 | Privacy-First Polish | ✅ Done — no INTERNET permission, all data local, export preference wired in Settings |

---

## Current File Inventory

### Source (`app/src/main/java/com/prime/frequently/`)
| File | Notes |
|---|---|
| `ui/MainActivity.kt` | Edge-to-edge host — nav graph wired in Phase 7 |
| `ui/HomeFragment.kt` | Skeleton — Phase 7 |
| `ui/LibraryFragment.kt` | ✅ Phase 4 — full: adapter, chip filters, search, nav to player |
| `ui/WavePresetAdapter.kt` | ✅ Phase 4 — ListAdapter, 2-col grid, band gradient thumbnails |
| `ui/HistoryFragment.kt` | ✅ Phase 6 — full: stats, 7-day bar chart, RecyclerView, swipe-to-delete, Clear All |
| `ui/SettingsFragment.kt` | Skeleton — Phase 8 |
| `ui/JourneyBuilderFragment.kt` | Skeleton — Phase 11.3 |
| `audio/SineWaveGenerator.kt` | ✅ Stereo PCM, continuous phase tracking, amplitude=1.0 output |
| `audio/NoiseGenerator.kt` | ✅ White, pink (Voss-McCartney 7-row), brown — all three generators complete |
| `audio/BinauralPlayer.kt` | ✅ Full: AudioTrack MODE_STREAM, MAX_PRIORITY thread, 500ms fade in/out, noise mixing wired |
| `audio/AudioEngine.kt` | ✅ Phase 11.2 — computeHz() linear interpolation + computeNoiseState() for waypoint transitions |
| `service/AudioForegroundService.kt` | ✅ Phase 9 — LocalBinder, audio focus, headphone receiver, notification (Pause/Resume/Stop actions) |
| `data/SessionRecord.kt` | ✅ Phase 6 — @Entity, @PrimaryKey wired |
| `data/WavePreset.kt` | Data class + WaveCategory enum |
| `data/FrequencyJourney.kt` | ✅ Phase 11.2 — includes totalDurationMinutes |
| `data/FrequencyWaypoint.kt` | ✅ Phase 11.2 — includes noiseType + noiseVolume per waypoint |
| `data/JourneyPresets.kt` | ✅ Phase 11.2 — FLOW_STATE, WIND_DOWN, DEEP_MEDITATION, STUDY_SESSION built-in journeys |
| `data/SessionIntent.kt` | ✅ Phase 11.1 — 7 intents with label, description, emoji |
| `data/IntentRecommendation.kt` | ✅ Phase 11.1 — recommendation data class |
| `repository/IntentRecommendationEngine.kt` | ✅ Phase 11.1 — maps SessionIntent → IntentRecommendation (journey or static Hz) |
| `data/AppDatabase.kt` | ✅ Phase 6 — @Database(SessionRecord), Room.databaseBuilder singleton |
| `data/SessionDao.kt` | ✅ Phase 6 — @Dao, insert/getAll/deleteById/deleteAll wired |
| `data/JourneyDao.kt` | Interface stub — Phase 11.3 |
| `viewmodel/HomeViewModel.kt` | ✅ AndroidViewModel; binds to AudioForegroundService; StateFlows wired; auto-saves SessionRecord on stop/complete; Phase 11.1 applyIntent(); Phase 11.2 startJourney() + journey tick coroutine |
| `viewmodel/HistoryViewModel.kt` | ✅ Phase 6 — AndroidViewModel, sessions Flow, stats (total/minutes/streak/weekCounts) |
| `viewmodel/PresetsViewModel.kt` | ✅ category filter + search query StateFlows |
| `repository/SessionRepository.kt` | ✅ Phase 6 — wired to AppDatabase, all CRUD ops |
| `repository/PresetRepository.kt` | Delegates to WavePresets constants |
| `constants/WavePresets.kt` | All 18 presets defined (Delta→Spiritual) |
| `constants/AppConstants.kt` | Sample rate, buffer size, notification IDs, Hz bounds |
| `utils/FrequencyUtils.kt` | beatHz→WaveCategory, left/right Hz helpers |
| `utils/TimeUtils.kt` | MM:SS formatter, unit converters |

### Layouts
| File | Notes |
|---|---|
| `layout/activity_main.xml` | CoordinatorLayout — NavHostFragment + BottomNavigationView |
| `layout/fragment_library.xml` | Preset browser — search bar, band filter chips, RecyclerView |
| `layout/fragment_player.xml` | Player — visualizer placeholder, progress, controls, bottom panel sliders |
| `layout/fragment_builder.xml` | Journey builder — timeline bar, segment list, add button |
| `layout/fragment_history.xml` | History — 3-col stat grid, BarChart, session RecyclerView |
| `layout/fragment_settings.xml` | Settings — FragmentContainerView for PreferenceFragment |
| `layout/fragment_custom_hz.xml` | Custom Hz — large Hz display, SeekBar, range labels, play button |
| `layout/fragment_timer.xml` | Placeholder — Phase 3 |
| `layout/fragment_mixer.xml` | Placeholder — Phase 2 |
| `layout/fragment_saved.xml` | Placeholder — Phase 11.3 |
| `layout/fragment_onboarding.xml` | Placeholder — Phase 10 |

### Resources
| File | Notes |
|---|---|
| `values/strings.xml` | Full string set — all screen titles, actions, labels, band names |
| `values/colors.xml` | Full Deep Space palette — 17 semantic tokens + wave band swatches |
| `values/themes.xml` | Material3 Dark NoActionBar, all color roles wired |
| `values/dimens.xml` | Full spacing scale, component sizes, typography sizes |
| `values-night/themes.xml` | Dark variant (defers to base) |
| `navigation/nav_graph.xml` | Full nav graph — 9 fragments, all actions wired |
| `menu/bottom_nav_menu.xml` | 5-item bottom nav (Library, Player, Build, History, Settings) |
| `drawable/` | 37 drawables — all icons (ic_*) + all backgrounds (bg_*) + color selectors |
| `AndroidManifest.xml` | ✅ Phase 9 — FOREGROUND_SERVICE, FOREGROUND_SERVICE_MEDIA_PLAYBACK, WAKE_LOCK, POST_NOTIFICATIONS; service declared |

### Docs
| File | Notes |
|---|---|
| `docs/DevelopmentPlan.md` | Full specification — source of truth for features |

---

## Preset Wave Categories

| Band | Freq Range | Use Case |
|---|---|---|
| Delta | 0.5–4 Hz | Sleep, healing |
| Theta | 4–8 Hz | Meditation, creativity |
| Alpha | 8–13 Hz | Calm focus, relaxation |
| Beta | 13–30 Hz | Active focus, study |
| Gamma | 30–100 Hz | Peak cognition |

Carrier Hz ranges: Delta/Theta 150–200 Hz, Alpha/Beta 200–300 Hz, Gamma 400 Hz.

---

## UI Design System (`frequently-ui-design/`)

> **Source of truth for all visual decisions.** Built in React/JSX by Claude Design. All Android UI must faithfully translate these specs.

### Design Language — Deep Space

The app is **dark-only**. No light mode. Background is near-black with a cosmic gradient overlay.

| Token | Hex | Role |
|---|---|---|
| `bg_0` | `#05060D` | Deepest background, screen base |
| `bg_1` | `#0A0C18` | Cards, bottom sheet, surface |
| `bg_2` | `#10132A` | Elevated surface |
| `bg_3` | `#181C3A` | Highlighted surface |
| `line` | `rgba(255,255,255,0.08)` | Subtle divider/border |
| `line_2` | `rgba(255,255,255,0.14)` | Stronger border |
| `ink` | `#F3F1FF` | Primary text |
| `ink_dim` | `#A5A3C9` | Secondary text |
| `ink_mute` | `#6C6A92` | Tertiary/label text |
| `violet` | `#8B7DFF` | Primary accent (binaural) |
| `violet_2` | `#A594FF` | Active/selected state |
| `cyan_accent` | `#5EF0E3` | Secondary accent |
| `magenta_accent` | `#FF7FD4` | Tertiary accent |
| `amber_accent` | `#FFC56B` | Warning / streak |
| `danger` | `#FF6A8B` | Error / destructive |

**Cosmic background**: Multi-layered radial gradients (violet top-center, cyan bottom-right, magenta bottom-left) over `bg_0`, with a subtle star-field dot pattern overlay (`::before` pseudo).

### Typography

| Usage | Font | Weight | Kotlin mapping |
|---|---|---|---|
| Hero numbers, screen titles | Space Grotesk | 400–500, `letterSpacing -0.02em` | `@font/space_grotesk_*` → system `sans-serif-medium` until .ttf bundled |
| Hz readouts, labels, chips, mono values | JetBrains Mono | 400, `letterSpacing -0.01em` | `@font/jetbrains_mono` → system `monospace` until .ttf bundled |
| Body text (descriptions, captions) | Inter / system-ui | 400 | default `sans-serif` |

**Font files to add later**: `space_grotesk_regular.ttf`, `space_grotesk_medium.ttf`, `space_grotesk_bold.ttf`, `jetbrains_mono_regular.ttf` → drop into `res/font/` and restore `@font/` references.

### Screens & Navigation

| Screen | File | Nav entry |
|---|---|---|
| Library (preset browser) | `screens-static.jsx → LibraryScreen` | Bottom nav: Browse |
| Player | `screen-player.jsx → PlayerScreen` | Bottom nav: Player |
| Track Builder | `screen-builder.jsx → BuilderScreen` | Bottom nav: Build |
| History | `screens-static.jsx → HistoryScreen` | Bottom nav: History |
| Custom Hz | `screens-static.jsx → CustomHzScreen` | Tap "+" on Library |
| Timer | `screens-static.jsx → TimerScreen` | Timer icon in Player |
| Background Mixer | `screens-static.jsx → MixerScreen` | Noise panel in Player |
| Saved Tracks | `screens-static.jsx → SavedScreen` | Saved tab / Builder |
| Onboarding | `screens-static.jsx → OnboardingScreen` | First launch only |
| Settings | `screens-static.jsx → SettingsScreen` | Bottom nav: Settings |

### Key UI Patterns

**Cards**: `background: rgba(255,255,255,0.03)`, `border: 1px solid rgba(255,255,255,0.08)`, `borderRadius: 20dp`. Use `bg_card.xml`.

**Chips**: Pill shape (`borderRadius: 999dp`), inactive = `rgba(255,255,255,0.04)` bg + `line` border + `ink_dim` text. Active = `rgba(139,125,255,0.18)` bg + violet border + `violet_2` text. Font: JetBrains Mono, 12sp.

**Icon buttons**: 40×40dp, `borderRadius: 12dp`, `rgba(255,255,255,0.03)` bg, `line` border, `ink_dim` icon.

**Primary button**: Full-width, `borderRadius: 16dp`, `linear-gradient(135deg, #8B7DFF, #5EF0E3)`, dark text `#0A0C18`, 14sp semibold.

**Toggles**: 42×24dp pill, ON = violet→cyan gradient, OFF = `rgba(255,255,255,0.10)`, white circle thumb.

**Sliders**: Custom-drawn — track `rgba(255,255,255,0.06)`, filled portion = band hue gradient, thumb = white circle with glow.

**Header pattern**: Back button (icon btn) | centered title (mono caption "FREQUENTLY" + display title) | optional right action.

**Section headers**: Left-aligned display title + right-aligned MONO UPPERCASE caption, 18sp / 10sp.

### Player Screen Design

- **Visualizer**: Full `Canvas`-drawn orbital animation — pulsing rings, core radial glow, two orbiting particles (left/right channels), dual sine waveforms top/bottom. Hue driven by wave band.
- **Preset title** below visualizer, 28sp Space Grotesk medium.
- **Freq line**: `{base} Hz · Δ {beat} Hz · {noise}` in JetBrains Mono.
- **Progress bar**: 4dp height, band-hue gradient fill with glow, white 12dp thumb.
- **Controls row**: Heart (like) | Prev | Play/Pause FAB (72dp, band gradient) | Next | Timer
- **Bottom sheet**: Tabs (Beat / Noise / Volume), band filter chips, three `SliderRow`s (Base Hz / Beat Hz / Vol).

### Custom Hz Screen Design

- Large gradient display number (72sp Space Grotesk, violet→cyan gradient text).
- **Keypad** (3-col grid) for direct numeric input of carrier Hz.
- Beat Hz via `SliderRow` (0.5–50 Hz range).
- Quick recall chips: Schumann 7.83 Hz, Love 528 Hz, OM 136.1 Hz, Solfeggio 417 Hz.
- Full-width gradient Play button at bottom.

### Builder Screen Design

- Editable track name (inline `EditText` in header).
- **Timeline bar**: horizontal proportional segments, each colored by band hue, selected segment has bright inset ring border.
- **Segment list**: drag-handle | index | MiniOrb | band + Hz | duration + noise | trash.
- **Segment editor panel**: wave band chip group, Base/Beat/Duration sliders, noise chip group, Preview button.

### History Screen Design

- **3-col stat grid**: Sessions (violet), Total time (cyan), Day streak (magenta), in small cards with large display number + MONO UPPERCASE label.
- **7-day bar chart**: custom bars with band-hue gradients + glow, day labels below.
- **Session list**: MiniOrb | title + STOPPED badge if incomplete | when · band · duration.

### Noise Types (8 total — not just 3)

`rain`, `ocean`, `forest`, `brown`, `pink`, `white`, `cafe`, `fire`

> The PLAN.md lists only white/pink/brown as generated internally. Rain, ocean, forest, cafe, fire are ambient recordings — implementation TBD (Phase 2 covers generated noise; ambient recordings are a Phase 10+ addition).

### Wave Band Colors (for UI tinting)

| Band | Hue | Swatch hi | Swatch lo |
|---|---|---|---|
| Delta | 220 (blue) | `#3B3A8F` | `#1A1B3A` |
| Theta | 265 (violet) | `#6B4FD1` | `#241A55` |
| Alpha | 195 (cyan) | `#3AB5C8` | `#12323A` |
| Beta | 160 (green) | `#3FC79A` | `#0E3128` |
| Gamma | 45 (amber) | `#FFB347` | `#3A2608` |

---

## Privacy Constraints

- **No `INTERNET` permission** — ever. This is a Play Store trust differentiator.
- No analytics SDKs, no Firebase, no ads.
- All data (Room DB + SharedPreferences) stays on device.
- User-initiated export only via Android Storage Access Framework.

---

*Last updated: Phase 9 completed*