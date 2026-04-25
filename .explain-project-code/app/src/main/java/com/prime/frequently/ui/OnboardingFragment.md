# OnboardingFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.OnboardingFragment

**Summary**
A placeholder Fragment for the first-launch onboarding flow (Phase 10 of the development plan). It inflates `fragment_onboarding.xml` and does nothing else — the multi-step onboarding UI, pager logic, and SharedPreferences gate ("has seen onboarding") are deferred to a later phase.

**Instance Variables**

_No instance variables._

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.OnboardingFragment.onCreateView`

**What** — Inflates and returns the `fragment_onboarding` layout as this fragment's root view.

**Why** — Keeps the Fragment registered in the navigation graph and compilable without crashing while the onboarding feature is not yet implemented.

**How**
1. Calls `inflater.inflate(R.layout.fragment_onboarding, container, false)` to build the view hierarchy from XML
2. Returns the inflated `View` directly

**Insights**
- Identical structure to other placeholder fragments (MixerFragment, SavedFragment, TimerFragment)
- When Phase 10 begins, this will gain a `ViewPager2` or step-based UI, a ViewModel for step tracking, and a SharedPreferences check in `MainActivity` to route first-time users here

---
