package com.prime.frequently.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionRecord)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<SessionRecord>>

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
