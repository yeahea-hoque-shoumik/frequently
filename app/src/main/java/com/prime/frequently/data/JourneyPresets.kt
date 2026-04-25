package com.prime.frequently.data

import com.prime.frequently.audio.NoiseType

object JourneyPresets {

    // 75 min: Beta 18 Hz → Alpha 10 Hz → Theta 6 Hz (5-min transitions)
    val FLOW_STATE = FrequencyJourney(
        id = "journey_flow_state",
        name = "Flow State Journey",
        totalDurationMinutes = 75,
        waypoints = listOf(
            FrequencyWaypoint(atMinute = 0,  carrierHz = 300.0, beatHz = 18.0, transitionDurationSecs = 0),
            FrequencyWaypoint(atMinute = 10, carrierHz = 250.0, beatHz = 10.0, transitionDurationSecs = 300),
            FrequencyWaypoint(atMinute = 20, carrierHz = 200.0, beatHz = 6.0,  transitionDurationSecs = 300)
        )
    )

    // 40 min: Alpha + pink → Theta + brown → Delta + brown (3-min transitions, auto-stop)
    val WIND_DOWN = FrequencyJourney(
        id = "journey_wind_down",
        name = "Wind Down Stack",
        totalDurationMinutes = 40,
        waypoints = listOf(
            FrequencyWaypoint(atMinute = 0,  carrierHz = 200.0, beatHz = 10.0, transitionDurationSecs = 0,
                noiseType = NoiseType.PINK,  noiseVolume = 0.3f),
            FrequencyWaypoint(atMinute = 10, carrierHz = 175.0, beatHz = 6.0,  transitionDurationSecs = 180,
                noiseType = NoiseType.BROWN, noiseVolume = 0.5f),
            FrequencyWaypoint(atMinute = 20, carrierHz = 150.0, beatHz = 2.0,  transitionDurationSecs = 120,
                noiseType = NoiseType.BROWN, noiseVolume = 0.7f)
        )
    )

    // 45 min: Alpha → Theta 6 Hz → Theta 5 Hz (5-min transitions)
    val DEEP_MEDITATION = FrequencyJourney(
        id = "journey_deep_meditation",
        name = "Deep Meditation Journey",
        totalDurationMinutes = 45,
        waypoints = listOf(
            FrequencyWaypoint(atMinute = 0,  carrierHz = 200.0, beatHz = 10.0, transitionDurationSecs = 0),
            FrequencyWaypoint(atMinute = 5,  carrierHz = 200.0, beatHz = 6.0,  transitionDurationSecs = 300),
            FrequencyWaypoint(atMinute = 15, carrierHz = 200.0, beatHz = 5.0,  transitionDurationSecs = 300)
        )
    )

    // 90 min: Alpha entry → Beta 15 Hz + pink noise (5-min transition)
    val STUDY_SESSION = FrequencyJourney(
        id = "journey_study",
        name = "Study Session Journey",
        totalDurationMinutes = 90,
        waypoints = listOf(
            FrequencyWaypoint(atMinute = 0, carrierHz = 200.0, beatHz = 10.0, transitionDurationSecs = 0),
            FrequencyWaypoint(atMinute = 5, carrierHz = 300.0, beatHz = 15.0, transitionDurationSecs = 300,
                noiseType = NoiseType.PINK, noiseVolume = 0.3f)
        )
    )

    val ALL = listOf(FLOW_STATE, WIND_DOWN, DEEP_MEDITATION, STUDY_SESSION)
}
