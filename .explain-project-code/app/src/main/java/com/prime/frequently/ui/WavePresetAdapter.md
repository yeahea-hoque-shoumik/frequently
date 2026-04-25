# WavePresetAdapter.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.WavePresetAdapter

**Summary**
A `ListAdapter` for the preset browser `RecyclerView` in `LibraryFragment`. Each item is a `WavePreset` card rendered with a band-themed gradient thumbnail, the category name + beat Hz label in the band's accent color, the preset name, and the recommended duration. Efficient diffing is handled automatically by the `DiffUtil.ItemCallback` companion.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `onClick` | `(WavePreset) -> Unit` | Callback invoked when a preset card is tapped; passed by the caller to handle navigation or playback |

---

### Nested Class: WavePresetAdapter.ViewHolder

**Summary**
A minimal `RecyclerView.ViewHolder` that holds a reference to the `ItemPresetCardBinding` for type-safe view access during bind.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `binding` | `ItemPresetCardBinding` | View Binding for `item_preset_card.xml`; gives direct access to all child views |

---

### `onCreateViewHolder`

**Function Name:** `com.prime.frequently.ui.WavePresetAdapter.onCreateViewHolder`

**What** — Inflates a new preset card item view and wraps it in a `ViewHolder`.

**Why** — Called by `RecyclerView` when it needs a new view to display; inflating here and re-using via the RecyclerView pool avoids repeated layout inflation.

**How**
1. Inflates `ItemPresetCardBinding` using the parent context's `LayoutInflater`
2. Wraps the binding in a new `ViewHolder` and returns it

**Insights**
- `attachToRoot = false` (implicit in the binding inflate signature) is required — `RecyclerView` manages attachment itself

---

### `onBindViewHolder`

**Function Name:** `com.prime.frequently.ui.WavePresetAdapter.onBindViewHolder`

**What** — Binds a `WavePreset` to the views in the given `ViewHolder`, setting the gradient thumbnail, text labels, and click listener.

**Why** — Provides per-item visual differentiation via wave-band colors while keeping all card views in the same layout XML.

**How**
1. Retrieves the `WavePreset` for this position via `getItem(position)`
2. Sets a click listener on the card root that invokes `onClick(preset)`
3. Calls `bandColors(preset.category)` to get the three color resource IDs for this band
4. Resolves the color resources to `Int` ARGB values via `ContextCompat.getColor`
5. Builds a `GradientDrawable` with `TL_BR` (top-left to bottom-right) orientation using the hi and lo colors
6. Applies the card's corner radius from `dimen/radius_card_sm` and sets it as the thumbnail background
7. Sets `tvBandHz` text to `"CATEGORY · X.X Hz"` and colors it with the accent color
8. Sets `tvName` to the preset name and `tvDuration` to `"X min"`

**Insights**
- The gradient is created fresh on every bind; this is acceptable for a small list but could be cached per `WaveCategory` if performance becomes an issue on low-end devices
- Destructuring `val (hiColor, loColor, accentColor) = bandColors(...)` is idiomatic Kotlin for `Triple` unpacking

---

### `DIFF` (companion object)

**Function Name:** `com.prime.frequently.ui.WavePresetAdapter.DIFF`

**What** — A `DiffUtil.ItemCallback` that tells `ListAdapter` how to compare `WavePreset` items for efficient, animated list updates.

**Why** — `ListAdapter` requires a diff callback to compute the minimal set of insertions/deletions/moves when the list changes, avoiding full `notifyDataSetChanged` redraws.

**How**
- `areItemsTheSame`: compares `a.id == b.id` — stable identity check
- `areContentsTheSame`: uses Kotlin data class `==` (structural equality) — full content check

**Insights**
- `WavePreset` must be a `data class` (or implement `equals`) for `areContentsTheSame` to work correctly; if it is a plain class with reference equality, content changes would never be detected

---

### `bandColors`

**Function Name:** `com.prime.frequently.ui.WavePresetAdapter.bandColors`

**What** — Maps a `WaveCategory` enum value to a `Triple` of color resource IDs: gradient-high, gradient-low, and accent color.

**Why** — Centralises the band-to-color mapping so all card rendering uses a single source of truth. Keeping it in the companion object makes it callable without an adapter instance (e.g., from `LibraryFragment` or other UI components that need band colors).

**How**
- Uses a `when` expression to map each `WaveCategory` to its corresponding color resources from `R.color.*`
- `SPIRITUAL` reuses Theta's gradient colors but uses `magenta_accent` to differentiate it visually

**Insights**
- `SPIRITUAL` sharing Theta's gradient is a deliberate design choice (spiritual states correlate with theta-range frequencies)
- If new bands are added to `WaveCategory`, the Kotlin compiler will flag a non-exhaustive `when` here, preventing silent missing-case bugs

---
