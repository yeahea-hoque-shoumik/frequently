# SessionDao.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.SessionDao`

**Summary**
The Room Data Access Object for session history. Provides all CRUD operations needed by `SessionRepository`: inserting new sessions, streaming the full ordered list as a Flow, deleting individual sessions, and clearing all history.

**Instance Variables**

_No instance variables — this is an interface; Room generates the concrete implementation._

---

### `insert`

**Function Name:** `com.prime.frequently.data.SessionDao.insert`

**What** — Inserts a `SessionRecord` into the `sessions` table, replacing any existing row with the same primary key.

**Why** — `REPLACE` conflict strategy means an upsert: if a session is saved twice (e.g., updated on resume), the newer record wins cleanly.

**How**
1. Room-generated implementation executes `INSERT OR REPLACE INTO sessions ...` with the `SessionRecord` fields
2. `suspend` means it must be called from a coroutine; Room dispatches the query to its internal IO dispatcher

---

### `getAll`

**Function Name:** `com.prime.frequently.data.SessionDao.getAll`

**What** — Returns a `Flow<List<SessionRecord>>` that emits the complete session history sorted by `startTime` descending (newest first), and re-emits whenever the table changes.

**Why** — `Flow` enables reactive UI updates: the History screen automatically refreshes when a new session is saved or one is deleted, without any polling or manual refresh calls.

**How**
1. Room generates a `Flow` backed by a SQLite `SELECT * FROM sessions ORDER BY startTime DESC`
2. Room observes the table for changes and emits a new list on every insert, update, or delete

---

### `deleteById`

**Function Name:** `com.prime.frequently.data.SessionDao.deleteById`

**What** — Deletes the session with the given `id` from the table.

**Why** — Supports swipe-to-delete in the History screen without touching other records.

**How**
1. Executes `DELETE FROM sessions WHERE id = :id` with the provided string parameter
2. `suspend` — runs off the main thread via coroutine

---

### `deleteAll`

**Function Name:** `com.prime.frequently.data.SessionDao.deleteAll`

**What** — Deletes every row from the `sessions` table.

**Why** — Powers the "Clear All" action in the History screen, giving users a way to reset their entire session history.

**How**
1. Executes `DELETE FROM sessions`
2. `suspend` — runs off the main thread via coroutine

**Insights**
- `deleteAll` has no confirmation at the DAO level — the UI layer (HistoryFragment) must present a confirmation dialog before calling it.
- The combination of `Flow`-based `getAll` and `suspend` mutation methods is the standard Room + coroutines pattern: reads are reactive, writes are one-shot.
