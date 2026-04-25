package com.prime.frequently.data

import com.prime.frequently.audio.NoiseType

data class FrequencyWaypoint(
    val atMinute: Int,
    val carrierHz: Double,
    val beatHz: Double,
    val transitionDurationSecs: Int = 60,
    val noiseType: NoiseType = NoiseType.NONE,
    val noiseVolume: Float = 0f
)
