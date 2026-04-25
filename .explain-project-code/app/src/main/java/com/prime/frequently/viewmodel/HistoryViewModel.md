# HistoryViewModel.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.viewmodel`

---

## Data Class: com.prime.frequently.viewmodel.HistoryStats

**Summary**
An immutable snapshot of aggregated session statistics displayed on the History screen. Computed from the full session list on every update.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `totalSessions` | `Int` | Total number of recorded sessions |
| `totalMinutes` | `Int` | Sum of all session durations converted from seconds to minutes |
| `streakDays` | `Int` | Number of consecutive days (ending today) in which at least one qualifying session (≥ 5 min) occurred |
| `weekCounts` | `List<Int>` | Session counts for each of the last 7 days, index 0 = oldest day, index 6 = today |

---

## Class: com.prime.frequently.viewmodel.HistoryViewModel

**Summary**
The ViewModel for the History screen. It exposes the full session list and derived statistics as cold-to-hot `StateFlow`s backed by the `SessionRepository`. It also provides delete operations dispatched on the `viewModelScope` coroutine scope.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `repo` | `SessionRepository` | Data access layer for reading and deleting session records |
| `sessions` | `StateFlow<List<SessionRecord>>` | Hot stream of all session records, kept alive for 5 seconds after the last subscriber leaves |
| `stats` | `StateFlow<HistoryStats>` | Derived hot stream of aggregated statistics; recomputed whenever `sessions` emits |

---

### `delete`

**Function Name:** `com.prime.frequently.viewmodel.HistoryViewModel.delete`

**What** — Deletes a single session record by its ID, dispatched asynchronously on the ViewModel scope.

**Why** — Deletion is a suspend operation (Room requires off-main-thread execution); wrapping it in `viewModelScope.launch` ensures it outlives the calling Fragment but is cancelled if the ViewModel is cleared.

**How**
1. Launches a coroutine on `viewModelScope`
2. Calls `repo.deleteById(id)` inside the coroutine

**Insights**
- The `sessions` StateFlow will automatically re-emit after deletion since it is backed by Room's reactive `Flow`

---

### `deleteAll`

**Function Name:** `com.prime.frequently.viewmodel.HistoryViewModel.deleteAll`

**What** — Deletes all session records asynchronously.

**Why** — Provides a bulk-clear operation for the "Clear Data" action in Settings or a "Clear All" button in HistoryFragment.

**How**
1. Launches a coroutine on `viewModelScope`
2. Calls `repo.deleteAll()` inside the coroutine

**Insights**
- Same reactive update behaviour as `delete` — the `sessions` Flow will emit an empty list after this completes

---

### `computeStreak`

**Function Name:** `com.prime.frequently.viewmodel.HistoryViewModel.computeStreak`

**What** — Computes the current consecutive-day streak of qualifying sessions (≥ 5 minutes each) working backwards from today.

**Why** — A meaningful streak requires a minimum session duration to prevent micro-sessions from inflating the count. The backwards traversal from today is the standard streak algorithm used by habit-tracking apps.

**How**
1. Filters sessions to those with `actualDurationSecs >= 300` (5 minutes)
2. Maps each qualifying session to its `dayKey` (midnight-normalised epoch ms)
3. Collects into a `SortedSet` to deduplicate days and sort ascending
4. Reverses to get descending order (most recent first)
5. If the list is empty, returns 0
6. Computes `today` via `dayKey(System.currentTimeMillis())`
7. Walks the list: if the current day equals `expected`, increments streak and decrements `expected` by one day; if the current day is before `expected`, breaks (gap found)
8. Returns the accumulated streak count

**Insights**
- Multiple qualifying sessions on the same day count as one streak day (due to `toSortedSet()` deduplication)
- If no session occurred today, the streak still starts from today (not yesterday) — if yesterday had a session it counts, meaning a gap today does not yet break the streak until tomorrow
- `DAY_MILLIS = 86_400_000L` is a fixed constant that does not account for DST transitions; on DST change days the day boundary may be off by one hour (acceptable for a wellness app)

---

### `computeWeekCounts`

**Function Name:** `com.prime.frequently.viewmodel.HistoryViewModel.computeWeekCounts`

**What** — Returns a list of 7 integers representing session counts for each of the past 7 days, with index 0 being the oldest and index 6 being today.

**Why** — Powers the 7-day bar chart on the History screen, giving the user a visual sense of their recent usage pattern.

**How**
1. Records `now` and `today` (midnight-normalised)
2. Maps over `6 downTo 0` — for each `daysAgo` value:
   a. Computes `dayStart = today - daysAgo * DAY_MILLIS`
   b. Computes `dayEnd = dayStart + DAY_MILLIS`
   c. Counts sessions whose `startTime` falls in `[dayStart, dayEnd)`
3. Returns the resulting list (index 0 = 6 days ago, index 6 = today)

**Insights**
- Uses half-open range `in dayStart until dayEnd` which correctly excludes the start of the next day
- Does not apply the 5-minute qualifying filter — all sessions count for the chart, even short ones

---

### `dayKey`

**Function Name:** `com.prime.frequently.viewmodel.HistoryViewModel.dayKey`

**What** — Truncates an epoch millisecond timestamp to midnight of that day in the device's local timezone, returning the result as epoch milliseconds.

**Why** — Used to normalise session timestamps to whole-day granularity for streak and week-count calculations. Using `Calendar.getInstance()` respects the user's local timezone, so "today" matches what the user expects.

**How**
1. Creates a `Calendar` instance (uses device timezone)
2. Sets `timeInMillis` to the input epoch
3. Zeroes out hours, minutes, seconds, and milliseconds
4. Returns the resulting `timeInMillis` (start of that day at local midnight)

**Insights**
- Timezone-aware: two sessions at 11:59 PM and 12:01 AM local time will be on different streak days
- `Calendar.getInstance()` creates a new instance per call; caching the Calendar would be a micro-optimisation, but the call is infrequent

---
