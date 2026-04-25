# AudioForegroundService.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.service`

---

## Class: `com.prime.frequently.service.AudioForegroundService`

**Summary**
A stub Android `Service` that will host `BinauralPlayer` in a foreground context to enable background audio playback with a persistent notification. Currently contains only skeleton lifecycle overrides — full implementation is planned for Phase 9.

**Instance Variables**

_No instance variables._

---

### `onBind`

**Function Name:** `com.prime.frequently.service.AudioForegroundService.onBind`

**What** — Returns `null`, indicating this service does not support bound clients.

**Why** — Currently a started service (not a bound service). Phase 9 will likely replace this with a `Binder` to allow `HomeFragment` to call play/pause/stop directly.

**How**
1. Returns `null`

---

### `onStartCommand`

**Function Name:** `com.prime.frequently.service.AudioForegroundService.onStartCommand`

**What** — Called when the service is started via `startService()`; returns `START_STICKY` to request automatic restart if killed by the system.

**Why** — `START_STICKY` is the correct flag for a media playback service: if Android kills the service for memory, it should be restarted so audio continues (or is ready to resume) rather than dying silently.

**How**
1. Returns `START_STICKY`

---

### `onDestroy`

**Function Name:** `com.prime.frequently.service.AudioForegroundService.onDestroy`

**What** — Called when the service is being torn down; currently delegates to `super.onDestroy()`.

**Why** — Will be used in Phase 9 to stop `BinauralPlayer`, cancel the notification, and release the audio focus.

**How**
1. Calls `super.onDestroy()`

**Insights**
- Phase 9 will need to: call `startForeground()` in `onStartCommand` with a notification, wire `BinauralPlayer` start/stop to service lifecycle, acquire `AudioFocus`, and expose a `Binder` so the UI can control playback without `Intent` round-trips.
