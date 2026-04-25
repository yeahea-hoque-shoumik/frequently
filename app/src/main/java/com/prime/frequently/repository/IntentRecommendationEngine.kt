package com.prime.frequently.repository

import com.prime.frequently.audio.NoiseType
import com.prime.frequently.data.IntentRecommendation
import com.prime.frequently.data.JourneyPresets
import com.prime.frequently.data.SessionIntent

object IntentRecommendationEngine {

    fun recommend(intent: SessionIntent): IntentRecommendation = when (intent) {
        SessionIntent.DEEP_SLEEP -> IntentRecommendation(
            intent = intent, carrierHz = 150.0, beatHz = 2.0,
            noiseType = NoiseType.BROWN, noiseVolume = 0.6f, durationMinutes = 45
        )
        SessionIntent.MEDITATION -> IntentRecommendation(
            intent = intent, carrierHz = 200.0, beatHz = 5.0,
            noiseType = NoiseType.NONE, noiseVolume = 0f, durationMinutes = 20
        )
        SessionIntent.STUDY -> IntentRecommendation(
            intent = intent, carrierHz = 300.0, beatHz = 15.0,
            noiseType = NoiseType.PINK, noiseVolume = 0.3f, durationMinutes = 90
        )
        SessionIntent.CREATIVE -> IntentRecommendation(
            intent = intent, carrierHz = 200.0, beatHz = 7.0,
            noiseType = NoiseType.NONE, noiseVolume = 0f, durationMinutes = 30
        )
        SessionIntent.FLOW_STATE -> IntentRecommendation(
            intent = intent, carrierHz = 300.0, beatHz = 18.0,
            noiseType = NoiseType.NONE, noiseVolume = 0f, durationMinutes = 75,
            journey = JourneyPresets.FLOW_STATE
        )
        SessionIntent.ANXIETY_RELIEF -> IntentRecommendation(
            intent = intent, carrierHz = 200.0, beatHz = 8.0,
            noiseType = NoiseType.BROWN, noiseVolume = 0.2f, durationMinutes = 20
        )
        SessionIntent.WIND_DOWN -> IntentRecommendation(
            intent = intent, carrierHz = 200.0, beatHz = 10.0,
            noiseType = NoiseType.PINK, noiseVolume = 0.3f, durationMinutes = 40,
            journey = JourneyPresets.WIND_DOWN
        )
    }
}
