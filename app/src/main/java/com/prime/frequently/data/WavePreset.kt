package com.prime.frequently.data

import com.prime.frequently.audio.NoiseType

enum class WaveCategory { DELTA, THETA, ALPHA, BETA, GAMMA, SPIRITUAL, JOURNEY }

data class WavePreset(
    val id: String,
    val name: String,
    val nameArabic: String = "",
    val category: WaveCategory,
    val carrierHz: Double,
    val beatHz: Double,
    val description: String,
    val noiseType: NoiseType = NoiseType.NONE,
    val noiseVolume: Float = 0f,
    val recommendedDurationMin: Int = 20,
    val colorRes: Int = 0,
    val journey: FrequencyJourney? = null
)
