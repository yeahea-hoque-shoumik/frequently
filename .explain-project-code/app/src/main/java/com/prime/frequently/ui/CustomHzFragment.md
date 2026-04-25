# CustomHzFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: `com.prime.frequently.ui.CustomHzFragment`

**Summary**
The Custom Hz entry screen, allowing users to input an arbitrary carrier frequency via an on-screen numeric keypad and adjust the beat frequency with a slider. It also provides quick-recall chips for scientifically notable frequencies (Schumann, OM, Solfeggio, Delta). On Play, it pushes the chosen frequencies into `HomeViewModel` and navigates to the Player.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `_b` | `FragmentCustomHzBinding?` | Nullable ViewBinding backing field; null outside the view lifecycle |
| `b` | `FragmentCustomHzBinding` | Non-null ViewBinding accessor |
| `homeVm` | `HomeViewModel` | Shared ViewModel (activity-scoped) for pushing frequency changes and triggering playback |
| `inputBuffer` | `StringBuilder` | Digit buffer accumulating the user's carrier Hz keypad input; starts at "200" |
| `currentBeatHz` | `Double` | Current beat frequency derived from the slider position; starts at 7.8 Hz |

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.onCreateView`

**What** — Inflates the custom Hz layout and assigns the ViewBinding.

**How**
1. Inflates `FragmentCustomHzBinding`; assigns to `_b`; returns `b.root`

---

### `onViewCreated`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.onViewCreated`

**What** — Wires all UI components: back navigation, keypad, beat slider, quick-recall chips, initial display state, gradient, and the Play button.

**How**
1. Sets back button click → `findNavController().navigateUp()`
2. Calls `setupKeypad()`, `setupBeatSlider()`, `setupQuickRecall()`
3. Calls `updateCarrierDisplay()`, `updateBeatDisplay()`, `updatePreviewCard()` to populate initial state
4. Registers `doOnLayout` to call `applyGradient()` after the carrier TextView is measured
5. Play button: parses `inputBuffer` → clamps to `[CARRIER_HZ_MIN, CARRIER_HZ_MAX]` → calls `homeVm.setCarrierHz()`, `homeVm.setBeatHz()`, `homeVm.play()` → navigates to player via `action_customHz_to_player`

---

### `setupKeypad`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.setupKeypad`

**What** — Dynamically builds a 4-row numeric keypad (1–9, ⌫, 0, C) by inflating `TextView` buttons into `LinearLayout` rows and adding them to the layout.

**Why** — A custom keypad is used rather than system keyboard for a controlled, digit-only input experience matching the design spec.

**How**
1. Defines four rows of key labels
2. For each row: creates a horizontal `LinearLayout` with bottom margin
3. For each key: creates a styled `TextView` with `bg_card_sm` background, centered text, equal weight, and `onKeyTap` click handler
4. Adds the row to `b.keypadGrid`

---

### `onKeyTap`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.onKeyTap`

**What** — Processes a keypad tap: appends digits, handles backspace, handles clear, and updates all displays.

**Why** — Maintains `inputBuffer` as a 3-digit string representing the carrier Hz, clamping and live-previewing the value on every keystroke.

**How**
1. `⌫`: removes last character from buffer
2. `C`: clears buffer and resets to "1"
3. `.`: ignored (carrier Hz is integer only)
4. Digit: prevents leading zeros; appends if under 3 chars; replaces entirely if already 3 chars
5. Clamps the parsed value to the valid range
6. Calls `updateCarrierDisplay()`, `applyGradient()`, `updatePreviewCard()`
7. Pushes clamped value to `homeVm.setCarrierHz()` only when within valid range

---

### `setupBeatSlider`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.setupBeatSlider`

**What** — Wires the beat Hz `SeekBar` (0–99 progress) to map linearly onto 0.5–50 Hz in 0.5 Hz steps.

**How**
1. Attaches `onProgressChanged` listener: `currentBeatHz = 0.5 + progress * 0.5`; pushes to `homeVm`; updates displays
2. Syncs the slider's initial position to `homeVm.beatHz.value`

---

### `setupQuickRecall`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.setupQuickRecall`

**What** — Builds quick-tap chips for four notable frequencies (Schumann 7.83 Hz, OM 136 Hz, Solfeggio 417 Hz, Delta 2 Hz) and adds them to `b.chipGroupRecall`.

**Why** — Gives users one-tap access to scientifically/spiritually significant frequencies without typing.

**How**
1. Defines a list of `Recall` data objects with optional carrier and/or beat Hz values
2. For each recall: creates a styled chip `TextView` → sets click listener to `applyRecall(recall)` → adds to `chipGroupRecall`

---

### `applyRecall`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.applyRecall`

**What** — Applies a quick-recall value to the carrier Hz input, beat slider, or both, then refreshes all displays.

**How**
1. If `recall.carrierHz` is non-null: clamps it, updates `inputBuffer`, calls `homeVm.setCarrierHz()`, updates carrier display and gradient
2. If `recall.beatHz` is non-null: clamps it, updates `currentBeatHz`, calls `homeVm.setBeatHz()`, syncs slider position, updates beat display
3. Calls `updatePreviewCard()`

---

### `updateCarrierDisplay`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.updateCarrierDisplay`

**What** — Updates the large carrier Hz display text from `inputBuffer`.

**How**
1. Sets `b.tvCarrierDisplay.text` to the buffer content or "0" if empty

---

### `updateBeatDisplay`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.updateBeatDisplay`

**What** — Updates the beat Hz label with one decimal place.

**How**
1. Sets `b.tvBeatDisplay.text` to `"%.1f".format(currentBeatHz)`

---

### `updatePreviewCard`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.updatePreviewCard`

**What** — Refreshes the preview card showing left channel Hz, beat Hz, right channel Hz, and the wave band label.

**How**
1. Computes `left` and `right` Hz via `FrequencyUtils`
2. Determines `category` via `FrequencyUtils.beatHzToCategory(currentBeatHz)`
3. Updates four TextViews: `tvLeftHz`, `tvBeatPreview`, `tvRightHz`, `tvBandPreview`

---

### `applyGradient`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.applyGradient`

**What** — Applies a violet→cyan horizontal `LinearGradient` shader to the carrier Hz display TextView.

**Why** — Matches the design spec: the large carrier number uses a gradient text effect (violet `#A594FF` → cyan `#5EF0E3`).

**How**
1. Returns early if `tv.width == 0` (not yet laid out)
2. Creates a `LinearGradient` spanning the full width of the TextView
3. Assigns it to `tv.paint.shader` and calls `tv.invalidate()`

---

### `bandLabel`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.bandLabel`

**What** — Returns a human-readable band description string for a given `WaveCategory`.

**How**
1. `when` expression mapping each `WaveCategory` to its label string

---

### `onDestroyView`

**Function Name:** `com.prime.frequently.ui.CustomHzFragment.onDestroyView`

**What** — Nulls `_b` to release the ViewBinding reference and prevent memory leaks.

**How**
1. Calls `super.onDestroyView()`; sets `_b = null`

---

### Nested Class: `CustomHzFragment.Recall`

**Summary**
Private data class holding a quick-recall chip's display label and optional carrier/beat Hz values. Using `null` for either field means the chip only sets the other.

| Variable | Type | Description |
|----------|------|-------------|
| `label` | `String` | Chip display text |
| `carrierHz` | `Double?` | Carrier Hz to apply, or null if this chip only changes beat Hz |
| `beatHz` | `Double?` | Beat Hz to apply, or null if this chip only changes carrier Hz |
