# PresetsViewModel.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.viewmodel`

---

## Class: com.prime.frequently.viewmodel.PresetsViewModel

**Summary**
The ViewModel for the Library (preset browser) screen. It manages two filter inputs — a category chip selection and a text search query — and exposes a derived `StateFlow<List<WavePreset>>` that reactively combines both filters to produce the visible preset list. All preset data is read from the in-memory `PresetRepository`.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `repo` | `PresetRepository` | Source of all built-in `WavePreset` definitions; delegates to `WavePresets` constants |
| `_selectedCategory` | `MutableStateFlow<WaveCategory?>` | Currently active band filter; `null` means "All" (no filter) |
| `selectedCategory` | `StateFlow<WaveCategory?>` | Public read-only view of the active category filter |
| `_searchQuery` | `MutableStateFlow<String>` | Current text search string; empty string means no query |
| `searchQuery` | `StateFlow<String>` | Public read-only view of the search query |
| `presets` | `StateFlow<List<WavePreset>>` | Derived list of presets after applying category and search filters; initialized eagerly with the full preset list |

---

### `presets` (StateFlow — derived)

**Function Name:** `com.prime.frequently.viewmodel.PresetsViewModel.presets` (inline combine operator)

**What** — Combines `_selectedCategory` and `_searchQuery` into a single filtered list of `WavePreset`s and exposes it as a hot `StateFlow`.

**Why** — Using `combine` ensures the list automatically re-emits whenever either filter changes, with no manual "refresh" calls from the UI. `SharingStarted.Eagerly` means the list is computed immediately so the first frame of the UI has data without waiting for a subscriber.

**How**
1. `combine(_selectedCategory, _searchQuery)` emits a new pair whenever either upstream flow emits
2. If `cat != null`, calls `repo.getByCategory(cat)`; otherwise calls `repo.getAll()`
3. If `query` is non-blank, filters the list to presets whose `name` or `description` contains the query (case-insensitive)
4. Emits the resulting list
5. `stateIn(..., Eagerly, repo.getAll())` converts the cold `combine` flow to a hot `StateFlow` with the full preset list as the initial value

**Insights**
- `SharingStarted.Eagerly` (vs `WhileSubscribed`) is appropriate here because the preset list is static in-memory data — there is no cost to keeping it alive
- Both `name` and `description` are searched, enabling queries like "focus" to match preset descriptions even if the name is more abstract
- The filter is case-insensitive (`ignoreCase = true`) for user-friendly search

---

### `selectCategory`

**Function Name:** `com.prime.frequently.viewmodel.PresetsViewModel.selectCategory`

**What** — Sets the active category filter, triggering the `presets` flow to re-emit a filtered list.

**Why** — Decouples the chip-selection UI event from the filtering logic; the UI only needs to call this and the `presets` StateFlow handles the rest reactively.

**How**
1. Assigns `category` to `_selectedCategory.value`
2. Passing `null` clears the filter (shows all presets)

**Insights**
- Passing the same category that is already selected is a no-op (StateFlow equality check prevents unnecessary re-emission)

---

### `setSearchQuery`

**Function Name:** `com.prime.frequently.viewmodel.PresetsViewModel.setSearchQuery`

**What** — Updates the search query string, triggering the `presets` flow to re-filter.

**Why** — Called on every text change in the search bar; the reactive pipeline ensures debouncing or additional logic can be inserted without changing the UI code.

**How**
1. Assigns `query` to `_searchQuery.value`

**Insights**
- No debounce is applied — for an in-memory list of 18 presets this is fine; for a large database-backed list, debouncing with `debounce(300)` before `combine` would be appropriate

---
