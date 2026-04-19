package com.prime.frequently.utils

import com.prime.frequently.data.WaveCategory

object FrequencyUtils {

    fun beatHzToCategory(beatHz: Double): WaveCategory = when {
        beatHz < 4.0  -> WaveCategory.DELTA
        beatHz < 8.0  -> WaveCategory.THETA
        beatHz < 13.0 -> WaveCategory.ALPHA
        beatHz < 30.0 -> WaveCategory.BETA
        else          -> WaveCategory.GAMMA
    }

    fun leftHz(carrierHz: Double, beatHz: Double): Double = carrierHz
    fun rightHz(carrierHz: Double, beatHz: Double): Double = carrierHz + beatHz
}
