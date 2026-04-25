# JourneyDao.kt

**Language:** Kotlin
**Package:** `com.prime.frequently.data`

---

## Class: `com.prime.frequently.data.JourneyDao`

**Summary**
A stub interface defining the Room DAO contract for persisting and retrieving user-created `FrequencyJourney` records. Not yet annotated with Room annotations — reserved for Phase 11.3 (Journey Builder).

**Instance Variables**

_No instance variables — this is an interface._

---

### `insert`

**Function Name:** `com.prime.frequently.data.JourneyDao.insert`

**What** — Persists a `FrequencyJourney` to the database.

**Why** — Called when the user saves a new or edited journey in the Journey Builder.

**How**
1. Stub — implementation to be provided by Room code generation once `@Insert` annotation is added.

---

### `getAll`

**Function Name:** `com.prime.frequently.data.JourneyDao.getAll`

**What** — Returns a `Flow` that emits the full list of saved journeys, updating automatically on any database change.

**Why** — A `Flow`-based query lets the UI (Builder/Saved screens) react to journey additions or deletions in real time without manual refresh logic.

**How**
1. Stub — implementation to be provided by Room once `@Query` annotation is added.

---

### `deleteById`

**Function Name:** `com.prime.frequently.data.JourneyDao.deleteById`

**What** — Removes a single journey by its string ID.

**Why** — Allows users to delete saved journeys from the Journey Builder without affecting other records.

**How**
1. Stub — implementation to be provided by Room once `@Query("DELETE FROM ...")` annotation is added.

**Insights**
- The interface currently lacks Room annotations (`@Dao`, `@Insert`, `@Query`) — it will not compile as a Room DAO until Phase 11.3 adds them and `AppDatabase` registers `FrequencyJourney` as an entity.
