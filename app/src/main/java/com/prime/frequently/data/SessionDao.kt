package com.prime.frequently.data

import kotlinx.coroutines.flow.Flow

// Phase 6: Room DAO — @Dao, @Insert, @Query annotations added when Room is wired up
interface SessionDao {
    fun insert(session: SessionRecord)
    fun getAll(): Flow<List<SessionRecord>>
    fun deleteById(id: String)
    fun deleteAll()
}
