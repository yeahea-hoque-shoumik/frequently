package com.prime.frequently.audio

// Phase 1: generates interleaved stereo PCM float buffers for binaural beats
class SineWaveGenerator(private val sampleRate: Int = 44100) {
    private var phase = 0.0

    fun generateStereoBuffer(
        freqLeft: Double,
        freqRight: Double,
        amplitude: Float,
        numFrames: Int
    ): FloatArray {
        val buffer = FloatArray(numFrames * 2)
        for (i in 0 until numFrames) {
            val t = phase + i.toDouble() / sampleRate
            buffer[i * 2]     = (amplitude * Math.sin(2.0 * Math.PI * freqLeft  * t)).toFloat()
            buffer[i * 2 + 1] = (amplitude * Math.sin(2.0 * Math.PI * freqRight * t)).toFloat()
        }
        phase += numFrames.toDouble() / sampleRate
        return buffer
    }

    fun reset() { phase = 0.0 }
}
