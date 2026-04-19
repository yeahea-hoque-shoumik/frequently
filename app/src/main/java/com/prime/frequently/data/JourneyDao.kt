package com.prime.frequently.data

import kotlinx.coroutines.flow.Flow

// Phase 11.3: Room DAO for user-created journeys
interface JourneyDao {
    fun insert(journey: FrequencyJourney)
    fun getAll(): Flow<List<FrequencyJourney>>
    fun deleteById(id: String)
}
