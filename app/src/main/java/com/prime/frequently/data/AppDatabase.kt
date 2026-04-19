package com.prime.frequently.data

import android.content.Context

// Phase 6: RoomDatabase singleton — @Database annotation and Room.databaseBuilder added then
object AppDatabase {
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: AppDatabase.also { instance = it }
        }
    }

    fun sessionDao(): SessionDao { throw NotImplementedError("Phase 6") }
    fun journeyDao(): JourneyDao { throw NotImplementedError("Phase 11.3") }
}
