# HistoryFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: `com.prime.frequently.ui.HistoryFragment`

**Summary**
The session history screen. Displays a 3-stat summary grid (total sessions, total minutes, streak days), a 7-day bar chart of session counts, and a scrollable session list with swipe-to-delete and a Clear All action. Driven by `HistoryViewModel`.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `_b` | `FragmentHistoryBinding?` | Nullable ViewBinding backing field |
| `b` | `FragmentHistoryBinding` | Non-null ViewBinding accessor |
| `vm` | `HistoryViewModel` | Fragment-scoped ViewModel providing session list and computed stats |
| `adapter` | `HistoryAdapter` | RecyclerView adapter for the session list |

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.onCreateView`

**What** — Inflates the history layout.

**How**
1. Inflates `FragmentHistoryBinding`; assigns to `_b`; returns `b.root`

---

### `onViewCreated`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.onViewCreated`

**What** — Wires the RecyclerView, bar chart, listeners, and launches the lifecycle-aware coroutines that collect `sessions` and `stats` from the ViewModel.

**How**
1. Calls `setupRecyclerView()`, `setupBarChart()`, `setupListeners()`
2. Launches two coroutines inside `repeatOnLifecycle(STARTED)`:
   - Collects `vm.sessions` → calls `adapter.submitList(sessions)` on each emission
   - Collects `vm.stats` → updates the three stat TextViews and calls `updateBarChart(stats.weekCounts)`

**Insights**
- `repeatOnLifecycle(STARTED)` cancels collection when the fragment goes to the background and resumes when it returns to the foreground, preventing wasted work and UI updates on invisible views.

---

### `setupRecyclerView`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.setupRecyclerView`

**What** — Configures the session list RecyclerView with a `LinearLayoutManager`, attaches the adapter, and installs a left-swipe-to-delete `ItemTouchHelper`.

**How**
1. Sets `LinearLayoutManager` and `adapter` on `b.sessionList`
2. Creates an `ItemTouchHelper.SimpleCallback` for left-swipe only:
   - `onSwiped`: reads the swiped item from `adapter.currentList`, calls `vm.delete(session.id)`
   - `onChildDraw`: draws the danger red reveal background behind the swiping row
3. Attaches the helper to the RecyclerView

**Insights**
- The `onChildDraw` clip rect draws only the revealed portion of the danger background (right side of item), not the full row width.

---

### `setupBarChart`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.setupBarChart`

**What** — Configures the MPAndroidChart `BarChart` appearance: no description, no grid, no legend, day labels on X axis, transparent background, touch disabled.

**How**
1. Disables bar shadow, description, gridlines, legend, pinch zoom
2. Sets background to transparent
3. Configures X axis to bottom position with day-label text in `ink_mute` colour
4. Disables both Y axes
5. Disables touch interaction (the chart is read-only)

---

### `updateBarChart`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.updateBarChart`

**What** — Refreshes the bar chart with the latest 7-day session count data, labelled with localised day abbreviations.

**How**
1. Generates 7 day labels by stepping back from today using a `Calendar` and "EEE" format
2. Converts `weekCounts` to `BarEntry` objects
3. Creates a violet `BarDataSet`; disables per-bar value labels
4. Sets the X axis formatter to the day labels
5. Assigns the data to `b.barChart` with `barWidth = 0.6f` and calls `invalidate()`

---

### `setupListeners`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.setupListeners`

**What** — Wires the "Clear All" text button to show a confirmation `AlertDialog` before deleting all sessions.

**Why** — Deleting all sessions is irreversible; the confirmation step prevents accidental data loss.

**How**
1. Sets a click listener on `b.tvClearAll`
2. Shows an `AlertDialog` with title/message; positive button calls `vm.deleteAll()`; negative button dismisses

---

### `onDestroyView`

**Function Name:** `com.prime.frequently.ui.HistoryFragment.onDestroyView`

**What** — Nulls `_b` to release the ViewBinding and prevent memory leaks.

**How**
1. Calls `super.onDestroyView()`; sets `_b = null`
