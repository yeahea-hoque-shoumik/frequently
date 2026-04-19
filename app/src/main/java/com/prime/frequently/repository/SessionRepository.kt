package com.prime.frequently.repository

import com.prime.frequently.data.SessionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// Phase 6: single source of truth for session data between ViewModel and Room
class SessionRepository {
    fun getAll(): Flow<List<SessionRecord>> = emptyFlow()
    suspend fun insert(session: SessionRecord) {}
    suspend fun deleteById(id: String) {}
    suspend fun deleteAll() {}
}
