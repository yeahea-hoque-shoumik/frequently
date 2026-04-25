# SettingsFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.SettingsFragment

**Summary**
The app settings screen, implemented as a `PreferenceFragmentCompat` to leverage Android's built-in preference system. It embeds the standard preference RecyclerView inside a custom layout (`fragment_settings.xml`) so a title header appears above the preference list, and wires the "Clear Data" destructive action to a confirmation dialog backed by `SessionRepository`.

**Instance Variables**

_No instance variables._

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.SettingsFragment.onCreateView`

**What** — Builds the final view by wrapping the preference list produced by the parent class inside the app's custom `fragment_settings.xml` layout.

**Why** — `PreferenceFragmentCompat` generates its own RecyclerView view, but the design requires a title header above it. Overriding `onCreateView` lets the custom wrapper layout act as a shell while the parent class still manages the preference RecyclerView inside it.

**How**
1. Calls `super.onCreateView(...)` to get the preference RecyclerView created by `PreferenceFragmentCompat`
2. Inflates `fragment_settings.xml` as the outer root
3. Finds the `FrameLayout` with id `settings_container` inside the outer root
4. Adds the preference RecyclerView (`prefView`) into the container with `MATCH_PARENT` dimensions
5. Returns the outer root as the Fragment's view

**Insights**
- This is a necessary workaround because `PreferenceFragmentCompat` does not support adding a custom header natively without creating a custom `PreferenceGroupAdapter`
- The `FrameLayout` container in `fragment_settings.xml` must exist and match the id `settings_container`, or `addView` will throw a `NullPointerException`

---

### `onCreatePreferences`

**Function Name:** `com.prime.frequently.ui.SettingsFragment.onCreatePreferences`

**What** — Loads the preference XML resource and wires the "Clear Data" preference click handler.

**Why** — This is the required lifecycle hook for `PreferenceFragmentCompat`; the preference screen cannot be created in `onCreateView`. All preference wiring is done here so that preference objects are guaranteed to exist.

**How**
1. Calls `setPreferencesFromResource(R.xml.preferences, rootKey)` to inflate the preference hierarchy from XML
2. Calls `wireClearData()` to attach the click listener for the destructive clear-data preference

**Insights**
- `rootKey` is passed through to `setPreferencesFromResource` to support nested `PreferenceScreen` hierarchies (unused here but required by the API)

---

### `wireClearData`

**Function Name:** `com.prime.frequently.ui.SettingsFragment.wireClearData`

**What** — Finds the "Clear Data" preference and attaches a click listener that shows a confirmation dialog before deleting all session history.

**Why** — Deleting all session records is irreversible, so a two-step confirmation (tap preference → confirm in dialog) prevents accidental data loss.

**How**
1. Calls `findPreference<Preference>(AppConstants.PREF_CLEAR_DATA)` to locate the preference by its key; returns early silently via `?.` if not found
2. Sets `OnPreferenceClickListener` that, when triggered:
   a. Builds and shows an `AlertDialog` with a title, message, and two buttons
   b. On the positive ("Clear") button: launches a coroutine on `Dispatchers.IO` and calls `SessionRepository(requireContext()).deleteAll()` to wipe all Room records
   c. On the negative ("Cancel") button: dismisses without action
3. Returns `true` from the listener to indicate the click was consumed

**Insights**
- `Dispatchers.IO` is correct for the Room delete operation — it must not run on the main thread
- A new `SessionRepository` instance is created inline; in a stricter MVVM setup this should go through a ViewModel, but for a one-shot destructive action it is acceptable
- If the `PREF_CLEAR_DATA` key is missing from `R.xml.preferences`, `findPreference` returns `null` and the listener is never attached — silent failure, not a crash

---
