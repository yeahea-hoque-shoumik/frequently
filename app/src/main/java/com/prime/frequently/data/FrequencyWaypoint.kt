package com.prime.frequently.data

data class FrequencyWaypoint(
    val atMinute: Int,
    val carrierHz: Double,
    val beatHz: Double,
    val transitionDurationSecs: Int = 60
)
