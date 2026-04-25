# MainActivity.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: com.prime.frequently.ui.MainActivity

**Summary**
The single Activity that hosts the entire app. It sets up edge-to-edge display, inflates the root layout via View Binding, applies system bar insets so content is not obscured by the status/navigation bars, and wires the `BottomNavigationView` to the Jetpack Navigation `NavController` — enabling the five bottom-tab destinations to switch fragments automatically.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `binding` | `ActivityMainBinding` | View Binding reference to `activity_main.xml`; provides type-safe access to all root-level views |

---

### `onCreate`

**Function Name:** `com.prime.frequently.ui.MainActivity.onCreate`

**What** — Initialises the Activity: enables edge-to-edge rendering, inflates the layout, handles system window insets, and connects the bottom navigation bar to the Fragment nav graph.

**Why** — As the sole Activity in a single-Activity architecture, this is the one place where the navigation host and the bottom bar are linked. Doing it here keeps all navigation wiring in one place and lets Jetpack Navigation handle back-stack and destination highlighting automatically.

**How**
1. Calls `enableEdgeToEdge()` so the app draws behind system bars (status bar + nav bar)
2. Inflates `ActivityMainBinding` and sets it as the content view
3. Registers a `WindowInsetsCompat` listener on the root view:
   - Reads system-bar inset values (top = status bar height, bottom = nav bar height)
   - Applies top padding to `navHostFragment` so fragment content starts below the status bar
   - Applies bottom padding to `bottomNav` so the tab bar sits above the system navigation bar
4. Retrieves the `NavHostFragment` from the `FragmentManager` by resource ID
5. Calls `setupWithNavController` to bind `bottomNav` to the nav controller — tab selections trigger navigation actions and the selected tab icon updates automatically

**Insights**
- `enableEdgeToEdge()` requires explicit inset handling (step 3); omitting it would cause content to be clipped behind system bars
- `lateinit var binding` is safe here because `onCreate` always runs before any view access
- This is a minimal single-Activity shell — all real UI lives in Fragments navigated by the nav graph
- The inset listener returns `insets` (not `WindowInsetsCompat.CONSUMED`) so child views can also respond to insets if needed

---
