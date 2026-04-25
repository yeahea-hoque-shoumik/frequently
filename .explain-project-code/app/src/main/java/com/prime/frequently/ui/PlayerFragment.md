# PlayerFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.PlayerFragment

**Summary**
The main playback screen Fragment. It binds to the shared `HomeViewModel`, wires all player controls (play/pause, carrier Hz slider, volume slider), observes StateFlows to keep the UI in sync, manages the screen-on flag while audio plays, and handles headphone disconnect events by pausing playback automatically.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `vm` | `HomeViewModel` | Shared activity-scoped ViewModel — carries all playback state and commands |
| `_b` | `FragmentPlayerBinding?` | Nullable backing field for the View Binding; nulled on view destruction to prevent memory leaks |
| `b` | `FragmentPlayerBinding` | Non-null accessor for the binding; crashes intentionally if accessed after `onDestroyView` |
| `headphoneReceiver` | `BroadcastReceiver` | Anonymous broadcast receiver that pauses playback when wired headphones are unplugged |

---

### `headphoneReceiver` (anonymous BroadcastReceiver)

**Function Name:** `com.prime.frequently.ui.PlayerFragment.headphoneReceiver.onReceive`

**What** — Listens for `ACTION_AUDIO_BECOMING_NOISY` and pauses the player if it is currently playing.

**Why** — When wired headphones are unplugged the audio route switches to the speaker, which would expose binaural audio to the room. The noisy-audio broadcast is the standard Android mechanism to react to this.

**How**
1. Checks that the received intent action is `ACTION_AUDIO_BECOMING_NOISY`
2. Checks `vm.isPlaying.value` — only acts if audio is actively playing
3. Calls `vm.pause()` to stop audio output

**Insights**
- `RECEIVER_NOT_EXPORTED` flag (set at registration) restricts the receiver to intra-app broadcasts, required on Android 14+
- Bluetooth disconnects also trigger `ACTION_AUDIO_BECOMING_NOISY`, so this covers wireless headphones too

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.onCreateView`

**What** — Inflates `fragment_player.xml` via View Binding and returns the root view.

**Why** — Standard Fragment lifecycle method; doing inflation here (rather than in the constructor) is the correct pattern because the Fragment view is created and destroyed independently of the Fragment instance.

**How**
1. Inflates `FragmentPlayerBinding` and assigns it to `_b`
2. Returns `b.root` as the Fragment's content view

**Insights**
- Overrides the lifecycle correctly — `_b` is set here and cleared in `onDestroyView`

---

### `onViewCreated`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.onViewCreated`

**What** — Configures all view interactions and launches StateFlow collectors once the view is ready.

**Why** — This is the correct lifecycle hook for view setup — the view hierarchy is fully inflated and `viewLifecycleOwner` is available, enabling safe coroutine scoping tied to the view's lifetime.

**How**
1. Computes `carrierRange` and sets `sliderCarrier.max` so the SeekBar spans the full carrier Hz range
2. Reads `SharedPreferences` via `PreferenceManager` for user settings (headphone warning, keep screen on)
3. Wires `btnBack` to call `findNavController().navigateUp()`
4. Wires `btnPlayPause`: on click, if not already playing checks the headphone-warning preference and shows a `Snackbar` if headphones are absent; then calls `vm.togglePlayPause()`
5. Wires `sliderCarrier.OnSeekBarChangeListener`: on user-driven progress change, converts the SeekBar value back to Hz (`CARRIER_HZ_MIN + progress`) and calls `vm.setCarrierHz`
6. Wires `sliderVolume.OnSeekBarChangeListener`: on user-driven progress change, converts 0–100 to 0f–1f and calls `vm.setVolume`
7. Launches a coroutine bound to `Lifecycle.State.STARTED` (auto-cancelled when the Fragment stops) that fans out into six child collectors:
   - `isPlaying` → updates play/pause icon and manages `FLAG_KEEP_SCREEN_ON`
   - `carrierHz` → updates `tvCarrierHz` label and syncs `sliderCarrier` position (skipped when the slider is being dragged)
   - `beatHz` → updates `tvBeatHz` label
   - `volume` → updates `tvVolumePct` label and syncs `sliderVolume` (skipped when dragged)
   - `elapsedSeconds` → formats to MM:SS, updates `tvElapsed`, drives `seekbarProgress`
   - `remainingSeconds` → shows countdown (e.g., `-02:30`) or `∞` for unlimited sessions
   - `events` → shows a Toast when a timed session completes

**Insights**
- `repeatOnLifecycle(STARTED)` is the safe pattern for UI collection — it stops collection when the Fragment goes to the background and restarts when it returns, preventing stale UI updates
- The `!b.sliderCarrier.isPressed` / `!b.sliderVolume.isPressed` guards prevent the slider jumping back to the ViewModel's value while the user is mid-drag (a common two-way binding race condition)
- Six concurrent `launch` blocks inside one `repeatOnLifecycle` block is an efficient fan-out — all collectors share the same lifecycle scope

---

### `onStart`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.onStart`

**What** — Registers the `headphoneReceiver` broadcast receiver when the Fragment becomes visible.

**Why** — The receiver should only be active while the player screen is visible; registering in `onStart`/`onStop` pairs prevents unnecessary callbacks when the Fragment is in the back stack.

**How**
1. Calls `requireContext().registerReceiver` with the `headphoneReceiver`, an `IntentFilter` for `ACTION_AUDIO_BECOMING_NOISY`, and `RECEIVER_NOT_EXPORTED`

**Insights**
- `RECEIVER_NOT_EXPORTED` is mandatory on Android 14+ for dynamically registered receivers with no `DYNAMIC_RECEIVER_EXPLICIT_EXPORT_REQUIRED` exemption

---

### `onStop`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.onStop`

**What** — Unregisters the broadcast receiver and clears `FLAG_KEEP_SCREEN_ON` when the Fragment is no longer visible.

**Why** — Prevents the receiver from firing after the player is off-screen, and ensures the screen-on flag is always lifted when the user leaves this screen.

**How**
1. Calls `requireContext().unregisterReceiver(headphoneReceiver)`
2. Calls `window.clearFlags(FLAG_KEEP_SCREEN_ON)`

**Insights**
- Clearing the flag here is a defensive safety net; the `isPlaying` collector also clears it when playback stops

---

### `onDestroyView`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.onDestroyView`

**What** — Nulls the View Binding reference when the Fragment's view is destroyed.

**Why** — Fragments outlive their views (e.g., when on the back stack). Holding a binding reference after the view is destroyed is a memory leak.

**How**
1. Sets `_b = null`

**Insights**
- Standard View Binding lifecycle pattern — the `b` property will throw `NullPointerException` if accessed after this point, which surfaces bugs immediately rather than silently

---

### `areHeadphonesConnected`

**Function Name:** `com.prime.frequently.ui.PlayerFragment.areHeadphonesConnected`

**What** — Returns `true` if any wired or Bluetooth audio output device is currently connected.

**Why** — Binaural beats require headphones to work. This check gates the headphone warning shown when the user taps Play without headphones attached.

**How**
1. Retrieves the `AudioManager` system service
2. Calls `getDevices(GET_DEVICES_OUTPUTS)` to get the list of active output audio devices
3. Returns `true` if any device has type `TYPE_WIRED_HEADPHONES`, `TYPE_WIRED_HEADSET`, `TYPE_BLUETOOTH_A2DP`, or `TYPE_BLUETOOTH_SCO`

**Insights**
- `TYPE_WIRED_HEADSET` includes headsets with a microphone (3.5mm TRRS); `TYPE_WIRED_HEADPHONES` is output-only
- Bluetooth SCO is for headsets (call-quality), A2DP is for stereo headphones — covering both ensures wireless headphones are detected correctly
- USB audio devices (e.g., USB-C DACs) are not checked — a minor gap for audiophile users

---
