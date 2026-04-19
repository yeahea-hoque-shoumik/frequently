package com.prime.frequently.data

import java.util.UUID

// Phase 6: Room entity — one row per completed or interrupted session
data class SessionRecord(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = 0L,
    val plannedDurationSecs: Int = 0,
    val actualDurationSecs: Int = 0,
    val presetName: String = "",
    val carrierHz: Double = 200.0,
    val beatHz: Double = 10.0,
    val noiseType: String = "NONE",
    val noiseVolume: Float = 0f,
    val completed: Boolean = false,
    val intentName: String = ""
)
