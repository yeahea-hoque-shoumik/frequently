# TimerFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.TimerFragment

**Summary**
A placeholder Fragment for the session timer screen (Phase 3 of the development plan). It inflates `fragment_timer.xml` and does nothing else — the countdown UI, duration picker, and auto-stop logic are deferred. The timer functionality is already partially implemented inside `HomeViewModel`; this Fragment will expose that as a dedicated screen.

**Instance Variables**

_No instance variables._

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.TimerFragment.onCreateView`

**What** — Inflates and returns the `fragment_timer` layout as this fragment's root view.

**Why** — Keeps the Fragment accessible from the navigation graph while the timer UI is not yet implemented.

**How**
1. Calls `inflater.inflate(R.layout.fragment_timer, container, false)` to build the view hierarchy
2. Returns the inflated `View` directly

**Insights**
- Follows the same minimal stub pattern as MixerFragment, OnboardingFragment, and SavedFragment
- When Phase 3 UI work begins, this will wire to `HomeViewModel`'s `durationSeconds`, `remainingSeconds`, and timer-control functions

---
