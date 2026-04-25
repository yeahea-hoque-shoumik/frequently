# AppDatabase.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.AppDatabase`

**Summary**
The Room database singleton for the app. It is the single access point for all local persistence, currently exposing one DAO for session history. The companion object implements thread-safe double-checked locking to ensure only one database instance exists per process.

**Instance Variables**

_No instance variables (abstract class — Room generates the implementation)._

---

### `sessionDao`

**Function Name:** `com.prime.frequently.data.AppDatabase.sessionDao`

**What** — Returns the `SessionDao` interface for querying and modifying session history records.

**Why** — Room requires abstract DAO accessor methods on the database class; the generated implementation binds the DAO to the database connection.

**How**
1. Abstract method — Room's code generator provides the concrete implementation at compile time via KSP.

---

### Nested Class: `AppDatabase.Companion`

**Summary**
Holds the singleton instance and the thread-safe factory method for obtaining it.

**Instance Variables**

| Variable | Type | Description |
|----------|------|-------------|
| `INSTANCE` | `AppDatabase?` | The single shared database instance; `@Volatile` ensures visibility across threads |

---

#### `getInstance`

**Function Name:** `com.prime.frequently.data.AppDatabase.Companion.getInstance`

**What** — Returns the existing `AppDatabase` instance or creates one if it doesn't yet exist, using double-checked locking for thread safety.

**Why** — Room database creation is expensive and must not run on the main thread; a singleton guarantees only one connection pool exists, preventing data corruption from concurrent writes.

**How**
1. Returns `INSTANCE` immediately if it is non-null (fast path — no lock needed)
2. Acquires a lock on `this` (the companion object)
3. Inside the lock, checks `INSTANCE` again (second check handles the race between two threads both passing step 1)
4. If still null: calls `Room.databaseBuilder` with `context.applicationContext`, the class reference, and the DB filename `"frequently_db"`; calls `.build()`; stores in `INSTANCE` via `also`
5. Returns the instance

**Insights**
- Using `context.applicationContext` prevents a leaked Activity/Fragment context from being held for the lifetime of the singleton.
- `@Volatile` on `INSTANCE` ensures that once thread A writes the new instance, thread B reads the updated value rather than a cached null from its CPU register.
- `exportSchema = false` in the `@Database` annotation suppresses schema export JSON files; this is acceptable for early development but should be enabled before shipping to support Room migrations.
