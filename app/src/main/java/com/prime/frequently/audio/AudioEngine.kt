package com.prime.frequently.audio

import com.prime.frequently.data.FrequencyWaypoint

object AudioEngine {

    /**
     * Returns the interpolated (carrierHz, beatHz) at the given elapsed seconds.
     *
     * Waypoint model:
     *   - waypoints[0]: initial Hz values (transitionDurationSecs ignored)
     *   - waypoints[i] (i>0): start transitioning FROM previous Hz TO these Hz
     *     starting at atMinute, completing after transitionDurationSecs
     *   - After the transition completes, hold at the waypoint Hz until the next one
     */
    fun computeHz(elapsedSecs: Long, waypoints: List<FrequencyWaypoint>): Pair<Double, Double> {
        if (waypoints.isEmpty()) return 200.0 to 10.0

        var current = waypoints[0]

        for (i in 1 until waypoints.size) {
            val wp = waypoints[i]
            val transStart = wp.atMinute * 60L
            val transEnd = transStart + wp.transitionDurationSecs

            when {
                elapsedSecs < transStart -> break
                wp.transitionDurationSecs > 0 && elapsedSecs < transEnd -> {
                    val t = (elapsedSecs - transStart).toDouble() / wp.transitionDurationSecs
                    val carrier = current.carrierHz + (wp.carrierHz - current.carrierHz) * t
                    val beat    = current.beatHz    + (wp.beatHz    - current.beatHz)    * t
                    return carrier to beat
                }
                else -> current = wp
            }
        }

        return current.carrierHz to current.beatHz
    }

    /**
     * Returns the noise state that should be active at the given elapsed seconds.
     * Noise switches at waypoint transition END boundaries (discrete, no interpolation).
     */
    fun computeNoiseState(elapsedSecs: Long, waypoints: List<FrequencyWaypoint>): Pair<NoiseType, Float> {
        if (waypoints.isEmpty()) return NoiseType.NONE to 0f

        var current = waypoints[0]

        for (i in 1 until waypoints.size) {
            val wp = waypoints[i]
            val transEnd = wp.atMinute * 60L + wp.transitionDurationSecs
            if (elapsedSecs >= transEnd) current = wp else break
        }

        return current.noiseType to current.noiseVolume
    }
}
