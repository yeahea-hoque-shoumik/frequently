# HistoryAdapter.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: `com.prime.frequently.ui.HistoryAdapter`

**Summary**
A `ListAdapter` for the session history `RecyclerView` in `HistoryFragment`. Binds `SessionRecord` items to `item_session` layout rows, colour-codes each row's orb dot by wave band, shows a "STOPPED" badge for incomplete sessions, and formats timestamps and durations.

**Instance Variables**

_No instance variables (state is managed by `ListAdapter` base class via `DiffUtil`)._

---

### `onCreateViewHolder`

**Function Name:** `com.prime.frequently.ui.HistoryAdapter.onCreateViewHolder`

**What** — Inflates the `item_session` layout and wraps it in a `VH`.

**How**
1. Inflates `ItemSessionBinding` from the parent context
2. Returns a new `VH` wrapping the binding

---

### `onBindViewHolder`

**Function Name:** `com.prime.frequently.ui.HistoryAdapter.onBindViewHolder`

**What** — Populates a session row with data from the corresponding `SessionRecord`.

**How**
1. Retrieves the `SessionRecord` at `position` via `getItem(position)`
2. Derives the `WaveCategory` from `beatHz` using `FrequencyUtils.beatHzToCategory`
3. Creates a circular `GradientDrawable` coloured by the band's highlight colour; sets it as `orbDot.background`
4. Sets `tvPresetName` to the preset name or "Custom" if empty
5. Shows/hides `tvStatusBadge` ("STOPPED") based on `s.completed`
6. Formats `startTime` with `DATE_FMT` ("MMM d, h:mm a") and sets `tvWhen`
7. Sets `tvBand` to the capitalised category name
8. Formats `actualDurationSecs` as "MM:SS" and sets `tvDuration`

---

### `bandHiColor`

**Function Name:** `com.prime.frequently.ui.HistoryAdapter.bandHiColor`

**What** — Maps a `WaveCategory` to its corresponding high-contrast colour resource ID for the orb dot.

**Why** — Each band has a distinct colour in the design system; using the `_hi` colour variant ensures the dot is visible against the dark card background.

**How**
1. `when` expression mapping each `WaveCategory` to the matching `R.color.band_*_hi` resource; Spiritual maps to `R.color.violet`

---

### Nested Class: `HistoryAdapter.Companion`

**Summary**
Holds the `DiffUtil.ItemCallback` and date formatter shared across all adapter instances.

| Variable | Type | Description |
|----------|------|-------------|
| `DIFF` | `DiffUtil.ItemCallback<SessionRecord>` | Compares items by `id` (identity) and by full equality (content) to enable efficient RecyclerView diffing |
| `DATE_FMT` | `SimpleDateFormat` | Locale-aware formatter producing "MMM d, h:mm a" strings for session timestamps |

---

### Nested Class: `HistoryAdapter.VH`

**Summary**
Minimal `RecyclerView.ViewHolder` that exposes the `ItemSessionBinding` for type-safe view access.

| Variable | Type | Description |
|----------|------|-------------|
| `b` | `ItemSessionBinding` | ViewBinding for the `item_session` row layout |
