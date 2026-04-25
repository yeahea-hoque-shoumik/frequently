# LibraryFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: `com.prime.frequently.ui.LibraryFragment`

**Summary**
The preset browser screen (Library tab). Displays all built-in presets in a 2-column grid with band-filter chips and a live search bar. Tapping a preset applies it to `HomeViewModel` and navigates to the Player. Also provides a "Custom Hz" button to enter the custom frequency screen.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `_b` | `FragmentLibraryBinding?` | Nullable ViewBinding backing field |
| `b` | `FragmentLibraryBinding` | Non-null ViewBinding accessor |
| `vm` | `PresetsViewModel` | Fragment-scoped ViewModel managing the filter state and filtered preset list |
| `homeVm` | `HomeViewModel` | Activity-scoped ViewModel used to apply the selected preset and trigger playback |

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.LibraryFragment.onCreateView`

**What** — Inflates the library layout.

**How**
1. Inflates `FragmentLibraryBinding`; assigns to `_b`; returns `b.root`

---

### `onViewCreated`

**Function Name:** `com.prime.frequently.ui.LibraryFragment.onViewCreated`

**What** — Sets up the preset grid, filter chips, search input, custom Hz button, and collects ViewModel state flows to keep the UI in sync.

**How**
1. Creates a `WavePresetAdapter` with a click lambda: `homeVm.applyPreset(preset)` → navigate to player
2. Configures `b.presetList` with a 2-column `GridLayoutManager` and the adapter
3. Builds a `chipMap` (chip TextView → optional WaveCategory), wires each chip's click → `vm.selectCategory(category)`
4. Attaches a `TextWatcher` to `b.searchInput`; on change calls `vm.setSearchQuery()`
5. Wires `b.btnCustomHz` → navigate to `action_library_to_customHz`
6. In `repeatOnLifecycle(STARTED)`:
   - Collects `vm.presets` → `adapter.submitList(presets)`
   - Collects `vm.selectedCategory` → `updateChipStyles(chipMap, selected)`

---

### `updateChipStyles`

**Function Name:** `com.prime.frequently.ui.LibraryFragment.updateChipStyles`

**What** — Updates each filter chip's background and text colour to reflect whether it is the currently active category.

**Why** — Provides visual feedback so users can see which band filter is active; uses `bg_chip_active` / `bg_chip` and `ink` / `ink_dim` colour tokens from the design system.

**How**
1. For each `(chip, category)` in `chipMap`:
   - Computes `isActive = category == selected`
   - Sets background to `bg_chip_active` if active, `bg_chip` otherwise
   - Sets text colour to `ink` if active, `ink_dim` otherwise

---

### `onDestroyView`

**Function Name:** `com.prime.frequently.ui.LibraryFragment.onDestroyView`

**What** — Nulls `_b` to release the ViewBinding reference.

**How**
1. Calls `super.onDestroyView()`; sets `_b = null`
