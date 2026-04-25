# SavedFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.SavedFragment

**Summary**
A placeholder Fragment for the saved journeys/tracks screen (Phase 11.3 of the development plan). It inflates `fragment_saved.xml` and does nothing else — the list of user-created journeys, playback controls, and import/export features are deferred to the Journey Builder phase.

**Instance Variables**

_No instance variables._

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.SavedFragment.onCreateView`

**What** — Inflates and returns the `fragment_saved` layout as this fragment's root view.

**Why** — Keeps the Fragment functional in the navigation graph while the saved-tracks feature is not yet implemented.

**How**
1. Calls `inflater.inflate(R.layout.fragment_saved, container, false)` to build the view hierarchy
2. Returns the inflated `View` directly

**Insights**
- Identical stub pattern used by MixerFragment, OnboardingFragment, and TimerFragment
- When Phase 11.3 begins, this will connect to `JourneyDao` and display a `RecyclerView` of saved `FrequencyJourney` records

---
