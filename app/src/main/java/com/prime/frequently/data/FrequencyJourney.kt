package com.prime.frequently.data

import com.prime.frequently.audio.NoiseType
import java.util.UUID

data class FrequencyJourney(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val waypoints: List<FrequencyWaypoint>,
    val noiseType: NoiseType = NoiseType.NONE,
    val noiseVolume: Float = 0f
)
