# FrequencyWaypoint.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.FrequencyWaypoint`

**Summary**
A single timed point in a `FrequencyJourney` timeline, specifying the target carrier and beat frequencies to reach at a given minute mark and how long the transition into those frequencies should take. Together, a list of waypoints defines a smooth frequency progression over a session.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `atMinute` | `Int` | The journey-relative minute at which this target frequency should be reached |
| `carrierHz` | `Double` | The carrier (base) frequency in Hz to target at this waypoint |
| `beatHz` | `Double` | The binaural beat frequency in Hz to target at this waypoint |
| `transitionDurationSecs` | `Int` | How many seconds to linearly interpolate from the previous waypoint's frequencies to this one (default: 60 s) |

---

_No methods — this is a pure data class._

**Insights**
- `transitionDurationSecs` defaults to 60 s, giving smooth 1-minute crossfades between waypoints by default.
- The `atMinute` field is a journey-relative offset, not a wall-clock time, so journeys are replayable at any time of day.
