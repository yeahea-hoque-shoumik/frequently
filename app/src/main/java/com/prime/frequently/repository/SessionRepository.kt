package com.prime.frequently.repository

import android.content.Context
import com.prime.frequently.data.AppDatabase
import com.prime.frequently.data.SessionRecord
import kotlinx.coroutines.flow.Flow

class SessionRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).sessionDao()

    fun getAll(): Flow<List<SessionRecord>> = dao.getAll()

    suspend fun insert(session: SessionRecord) = dao.insert(session)

    suspend fun deleteById(id: String) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()
}
