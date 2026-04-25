# Project Code Explanation Index

**Project:** frequently
**Path:** /Users/shoumik/AndroidStudioProjects/frequently
**Generated:** 2026-04-25
**Total Files Explained:** 39

---

## File Index

### `app/` (Build Scripts)
- [build.gradle.kts](app/build.gradle.md) — App-level Gradle build configuration: dependencies, AGP, KSP, Room, Navigation, MPAndroidChart

### `app/src/androidTest/java/com/prime/frequently/`
- [ExampleInstrumentedTest.kt](app/src/androidTest/java/com/prime/frequently/ExampleInstrumentedTest.md) — Scaffold instrumented test that verifies the app package name via AndroidJUnit4

### `app/src/main/java/com/prime/frequently/audio/`
- [AudioEngine.kt](app/src/main/java/com/prime/frequently/audio/AudioEngine.md) — Stub for the progressive frequency transition engine (Phase 11.2)
- [BinauralPlayer.kt](app/src/main/java/com/prime/frequently/audio/BinauralPlayer.md) — AudioTrack wrapper managing stereo PCM playback, fade in/out, and noise mixing on a MAX_PRIORITY thread
- [NoiseGenerator.kt](app/src/main/java/com/prime/frequently/audio/NoiseGenerator.md) — Generates white, pink (Voss-McCartney 7-row), and brown noise as float PCM samples
- [SineWaveGenerator.kt](app/src/main/java/com/prime/frequently/audio/SineWaveGenerator.md) — Stereo sine wave PCM generator with continuous phase tracking to prevent audio discontinuities

### `app/src/main/java/com/prime/frequently/constants/`
- [AppConstants.kt](app/src/main/java/com/prime/frequently/constants/AppConstants.md) — Global constants: sample rate, buffer size, Hz bounds, notification IDs, preference keys
- [WavePresets.kt](app/src/main/java/com/prime/frequently/constants/WavePresets.md) — Definitions for all 18 built-in binaural beat presets across Delta, Theta, Alpha, Beta, Gamma, and Spiritual bands

### `app/src/main/java/com/prime/frequently/data/`
- [AppDatabase.kt](app/src/main/java/com/prime/frequently/data/AppDatabase.md) — Room database singleton with SessionRecord entity and migration-safe builder
- [FrequencyJourney.kt](app/src/main/java/com/prime/frequently/data/FrequencyJourney.md) — Data class representing a named multi-waypoint frequency journey (Phase 11.2)
- [FrequencyWaypoint.kt](app/src/main/java/com/prime/frequently/data/FrequencyWaypoint.md) — Data class for a single timed segment within a FrequencyJourney
- [JourneyDao.kt](app/src/main/java/com/prime/frequently/data/JourneyDao.md) — Room DAO stub for journey CRUD operations (Phase 11.3)
- [SessionDao.kt](app/src/main/java/com/prime/frequently/data/SessionDao.md) — Room DAO for session record insert, query-all, delete-by-id, and delete-all
- [SessionRecord.kt](app/src/main/java/com/prime/frequently/data/SessionRecord.md) — Room entity capturing a single binaural session: timing, frequencies, noise, and completion status
- [WavePreset.kt](app/src/main/java/com/prime/frequently/data/WavePreset.md) — Data class for a binaural beat preset with category, Hz values, noise defaults, and metadata

### `app/src/main/java/com/prime/frequently/repository/`
- [PresetRepository.kt](app/src/main/java/com/prime/frequently/repository/PresetRepository.md) — In-memory preset repository delegating to WavePresets constants; supports get-all and filter-by-category
- [SessionRepository.kt](app/src/main/java/com/prime/frequently/repository/SessionRepository.md) — Room-backed repository providing reactive Flow access and CRUD operations for session records

### `app/src/main/java/com/prime/frequently/service/`
- [AudioForegroundService.kt](app/src/main/java/com/prime/frequently/service/AudioForegroundService.md) — Stub foreground service for background audio playback (Phase 9)

### `app/src/main/java/com/prime/frequently/ui/`
- [BuilderFragment.kt](app/src/main/java/com/prime/frequently/ui/BuilderFragment.md) — Placeholder Fragment for the drag-drop journey builder screen (Phase 11.3)
- [CustomHzFragment.kt](app/src/main/java/com/prime/frequently/ui/CustomHzFragment.md) — Custom Hz entry screen with numeric keypad, beat Hz slider, quick-recall chips, and live binaural preview
- [HistoryAdapter.kt](app/src/main/java/com/prime/frequently/ui/HistoryAdapter.md) — ListAdapter for the session history RecyclerView with swipe-to-delete and formatted time/band display
- [HistoryFragment.kt](app/src/main/java/com/prime/frequently/ui/HistoryFragment.md) — History screen: stat cards, 7-day bar chart, session RecyclerView with swipe-delete and Clear All
- [LibraryFragment.kt](app/src/main/java/com/prime/frequently/ui/LibraryFragment.md) — Preset browser with band filter chips, search bar, and grid RecyclerView navigating to the player
- [MainActivity.kt](app/src/main/java/com/prime/frequently/ui/MainActivity.md) — Single-Activity host: edge-to-edge setup, system inset handling, BottomNavigation wired to NavController
- [MixerFragment.kt](app/src/main/java/com/prime/frequently/ui/MixerFragment.md) — Placeholder Fragment for the background noise mixer screen (Phase 2 UI)
- [OnboardingFragment.kt](app/src/main/java/com/prime/frequently/ui/OnboardingFragment.md) — Placeholder Fragment for the first-launch onboarding flow (Phase 10)
- [PlayerFragment.kt](app/src/main/java/com/prime/frequently/ui/PlayerFragment.md) — Main player screen: play/pause, carrier/volume sliders, StateFlow collectors, headphone detection, screen-on flag
- [SavedFragment.kt](app/src/main/java/com/prime/frequently/ui/SavedFragment.md) — Placeholder Fragment for the saved journeys screen (Phase 11.3)
- [SettingsFragment.kt](app/src/main/java/com/prime/frequently/ui/SettingsFragment.md) — Settings screen (PreferenceFragmentCompat) with custom header layout and Clear Data confirmation dialog
- [TimerFragment.kt](app/src/main/java/com/prime/frequently/ui/TimerFragment.md) — Placeholder Fragment for the dedicated timer screen (Phase 3 UI)
- [WavePresetAdapter.kt](app/src/main/java/com/prime/frequently/ui/WavePresetAdapter.md) — ListAdapter for preset cards with band-gradient thumbnails, accent-colored Hz labels, and click callbacks

### `app/src/main/java/com/prime/frequently/utils/`
- [FrequencyUtils.kt](app/src/main/java/com/prime/frequently/utils/FrequencyUtils.md) — Stateless utility: maps beat Hz to WaveCategory brain-wave band; computes per-ear stereo frequencies
- [TimeUtils.kt](app/src/main/java/com/prime/frequently/utils/TimeUtils.md) — Stateless utility: formats seconds as MM:SS, converts minutes↔seconds and millis↔seconds

### `app/src/main/java/com/prime/frequently/viewmodel/`
- [HistoryViewModel.kt](app/src/main/java/com/prime/frequently/viewmodel/HistoryViewModel.md) — AndroidViewModel for the History screen: session StateFlow, aggregated stats (total, minutes, streak, week counts), delete operations
- [HomeViewModel.kt](app/src/main/java/com/prime/frequently/viewmodel/HomeViewModel.md) — Activity-scoped ViewModel owning BinauralPlayer, all playback state, countdown timer coroutine, and session persistence
- [PresetsViewModel.kt](app/src/main/java/com/prime/frequently/viewmodel/PresetsViewModel.md) — ViewModel for the Library screen: reactive combine of category filter and search query into a filtered preset StateFlow

### `app/src/test/java/com/prime/frequently/`
- [ExampleUnitTest.kt](app/src/test/java/com/prime/frequently/ExampleUnitTest.md) — Scaffold JVM unit test with a trivial arithmetic assertion (placeholder for real unit tests)

### `/` (Root Build Scripts)
- [build.gradle.kts](build.gradle.md) — Root-level Gradle build: AGP and Kotlin plugin declarations (apply false)
- [settings.gradle.kts](settings.gradle.md) — Gradle settings: project name, plugin repositories, JitPack for MPAndroidChart
