package com.prime.frequently.data

import com.prime.frequently.audio.NoiseType

data class IntentRecommendation(
    val intent: SessionIntent,
    val carrierHz: Double,
    val beatHz: Double,
    val noiseType: NoiseType,
    val noiseVolume: Float,
    val durationMinutes: Int,
    val journey: FrequencyJourney? = null
)
