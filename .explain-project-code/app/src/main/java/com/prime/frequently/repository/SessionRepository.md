# SessionRepository.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.repository`

---

## Class: `com.prime.frequently.repository.SessionRepository`

**Summary**
The mediator between ViewModels and the Room database for session history. It initialises the DAO from the database singleton and exposes clean coroutine-safe methods so ViewModels never interact with Room directly.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `dao` | `SessionDao` | The Room DAO instance obtained from `AppDatabase`; all database calls delegate to it |

---

### `getAll`

**Function Name:** `com.prime.frequently.repository.SessionRepository.getAll`

**What** — Returns a `Flow` that continuously emits the full session list, ordered newest-first, and updates whenever the table changes.

**Why** — Delegates to the DAO's reactive `Flow`-based query so the `HistoryViewModel` can observe session changes without polling.

**How**
1. Returns `dao.getAll()` directly

---

### `insert`

**Function Name:** `com.prime.frequently.repository.SessionRepository.insert`

**What** — Persists a new or updated `SessionRecord` to the database.

**Why** — Called by `HomeViewModel` when a session stops or completes, recording the outcome for history and streak calculations.

**How**
1. Delegates to `dao.insert(session)` as a suspend call

---

### `deleteById`

**Function Name:** `com.prime.frequently.repository.SessionRepository.deleteById`

**What** — Removes the session with the given `id` from the database.

**Why** — Powers the swipe-to-delete gesture in `HistoryFragment`.

**How**
1. Delegates to `dao.deleteById(id)` as a suspend call

---

### `deleteAll`

**Function Name:** `com.prime.frequently.repository.SessionRepository.deleteAll`

**What** — Deletes every session record from the database.

**Why** — Powers the "Clear All" button in `HistoryFragment` after the user confirms the destructive action.

**How**
1. Delegates to `dao.deleteAll()` as a suspend call

**Insights**
- The constructor takes a `Context` rather than a `SessionDao` directly. This is simple but makes unit testing harder because a real `AppDatabase` must be created. Injecting the DAO (or using a DI framework) would improve testability.
