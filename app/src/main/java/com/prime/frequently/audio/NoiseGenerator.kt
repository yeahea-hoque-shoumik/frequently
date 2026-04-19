package com.prime.frequently.audio

import kotlin.random.Random

enum class NoiseType { NONE, WHITE, PINK, BROWN }

class NoiseGenerator {
    private val random = Random.Default

    // ── White noise ───────────────────────────────────────────────────────────
    fun whiteNoiseSample(): Float = random.nextFloat() * 2f - 1f

    // ── Pink noise — Voss-McCartney algorithm ─────────────────────────────────
    // Sums 7 octave-scaled white-noise generators. Each generator updates only
    // when a specific bit of an incrementing counter flips — this produces the
    // 1/f spectral density characteristic of pink noise. Output is normalised
    // to ±1 by dividing by 7 (number of rows).
    private val pinkRows = FloatArray(7)
    private var pinkRunningSum = 0f
    private var pinkCounter = 0

    fun pinkNoiseSample(): Float {
        val lastCounter = pinkCounter
        pinkCounter = (pinkCounter + 1) and 0x7FFF

        // XOR tells us which bits flipped; each flipped bit maps to one row
        val diff = lastCounter xor pinkCounter
        for (bit in 0 until 7) {
            if (diff and (1 shl bit) != 0) {
                val newValue = random.nextFloat() * 2f - 1f
                pinkRunningSum += newValue - pinkRows[bit]
                pinkRows[bit] = newValue
            }
        }

        // Add fresh white noise to the sum, then scale to ±1
        val white = random.nextFloat() * 2f - 1f
        return ((pinkRunningSum + white) / 8f).coerceIn(-1f, 1f)
    }

    // ── Brown noise — running integration of white noise ──────────────────────
    // Integrates (accumulates) white noise with a small step, then clamps.
    // Produces a 1/f² spectrum — deep, rumbling, most calming.
    private var brownState = 0f

    fun brownNoiseSample(): Float {
        brownState = (brownState + (random.nextFloat() * 0.02f - 0.01f)).coerceIn(-1f, 1f)
        return brownState
    }

    // ── Reset (called on player stop/restart) ─────────────────────────────────
    fun reset() {
        pinkRows.fill(0f)
        pinkRunningSum = 0f
        pinkCounter = 0
        brownState = 0f
    }
}
