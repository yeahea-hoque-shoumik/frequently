# BuilderFragment.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.ui`

---

## Class: `com.prime.frequently.ui.BuilderFragment`

**Summary**
A skeleton Fragment for the Journey Builder screen (Phase 11.3). Currently inflates `fragment_builder.xml` and properly manages the ViewBinding lifecycle. All drag-drop timeline editing logic is deferred to Phase 11.3.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `_b` | `FragmentBuilderBinding?` | Nullable backing field for the view binding; null outside of the view lifecycle |
| `b` | `FragmentBuilderBinding` | Non-null accessor that throws if accessed outside `onCreateView`–`onDestroyView` |

---

### `onCreateView`

**Function Name:** `com.prime.frequently.ui.BuilderFragment.onCreateView`

**What** — Inflates the builder layout and returns the root view.

**Why** — Standard Fragment view creation lifecycle method; ViewBinding inflation is done here so the binding is available for the full view lifetime.

**How**
1. Inflates `FragmentBuilderBinding` using the provided `inflater` and `container`
2. Assigns the result to `_b`
3. Returns `b.root`

---

### `onDestroyView`

**Function Name:** `com.prime.frequently.ui.BuilderFragment.onDestroyView`

**What** — Nulls the binding reference when the view is destroyed.

**Why** — Fragments outlive their views (e.g., in the back stack). Nulling `_b` prevents the binding from holding a reference to destroyed views, avoiding a memory leak.

**How**
1. Calls `super.onDestroyView()`
2. Sets `_b = null`
