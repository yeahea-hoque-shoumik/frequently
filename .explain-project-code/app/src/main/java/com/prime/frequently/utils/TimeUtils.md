# TimeUtils.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.utils`

---

## Class: com.prime.frequently.utils.TimeUtils (object)

**Summary**
A stateless singleton utility object that provides time-unit conversion and formatting helpers used across the timer system, session history display, and progress tracking.

**Instance Variables**

_No instance variables._

---

## Top-Level Functions

### `secondsToMmSs`

**Function Name:** `com.prime.frequently.utils.TimeUtils.secondsToMmSs`

**What** — Converts a total number of seconds into a zero-padded `"MM:SS"` string (e.g., `125` → `"02:05"`).

**Why** — The player and timer displays require a human-readable time format; centralising this prevents inconsistent formatting across multiple screens.

**How**
1. Divides `totalSeconds` by 60 to get minutes
2. Computes `totalSeconds % 60` to get remaining seconds
3. Formats both with `%02d` (zero-padded two digits) separated by `:`

**Insights**
- Does not handle hours — values ≥ 3600 seconds will display incorrect minutes (e.g., 3661 → `"61:01"`); acceptable given session durations are unlikely to exceed an hour
- No negative-value guard; negative input would produce negative-digit strings

---

### `minutesToSeconds`

**Function Name:** `com.prime.frequently.utils.TimeUtils.minutesToSeconds`

**What** — Converts a duration in whole minutes to its equivalent in seconds.

**Why** — Preset recommended durations and timer picker values are expressed in minutes; the audio timer and coroutine countdown operate in seconds. This conversion bridges the two domains.

**How**
1. Returns `minutes * 60`

**Insights**
- Simple multiplication with no overflow protection; for practical session durations (< 120 min) there is no risk of `Int` overflow

---

### `millisToSeconds`

**Function Name:** `com.prime.frequently.utils.TimeUtils.millisToSeconds`

**What** — Converts a `Long` millisecond value to an `Int` number of seconds by integer division, truncating the fractional part.

**Why** — Android system time APIs (e.g., `SystemClock.elapsedRealtime()`, `System.currentTimeMillis()`) return milliseconds; the timer and session duration tracking work in whole seconds.

**How**
1. Divides `millis` by 1000 (integer division, discards sub-second remainder)
2. Casts the `Long` result to `Int` for compatibility with the rest of the timer system

**Insights**
- The `Long → Int` cast is safe for session durations up to ~2 billion seconds (≈ 68 years) — no practical risk
- Truncation (not rounding) means a 1999 ms value returns `1`, not `2` — consistent with floor semantics expected by elapsed-time displays

---
