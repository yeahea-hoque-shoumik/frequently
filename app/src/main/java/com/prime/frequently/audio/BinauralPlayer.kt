package com.prime.frequently.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.prime.frequently.constants.AppConstants

class BinauralPlayer {

    // ── Playback parameters (written from main thread, read from audio thread) ──
    @Volatile var freqLeft: Double = 200.0
    @Volatile var freqRight: Double = 210.0
    @Volatile var amplitude: Float = 0.5f
    @Volatile var noiseType: NoiseType = NoiseType.NONE
    @Volatile var noiseVolume: Float = 0f

    // ── Thread state ──────────────────────────────────────────────────────────
    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null

    @Volatile private var isActive = false
    @Volatile private var isPaused = false
    @Volatile private var fadingOut = false

    // ── Envelope for fade in / fade out ──────────────────────────────────────
    // Per-sample ramp: 0→1 or 1→0 over FADE_DURATION_MS (500 ms)
    private var envelope = 0f
    private val fadeStep = 1f / (AppConstants.FADE_DURATION_MS * AppConstants.SAMPLE_RATE / 1000f)

    private val sineGen = SineWaveGenerator(AppConstants.SAMPLE_RATE)
    private val noiseGen = NoiseGenerator()

    val isPlaying: Boolean get() = isActive && !isPaused

    // ── Public controls ───────────────────────────────────────────────────────

    fun start() {
        if (isActive) return
        isActive = true
        isPaused = false
        fadingOut = false
        envelope = 0f

        audioTrack = buildAudioTrack().also { it.play() }

        playbackThread = Thread(::runLoop, "BinauralPlayer").also {
            it.priority = Thread.MAX_PRIORITY
            it.start()
        }
    }

    fun pause() {
        if (!isActive || isPaused) return
        isPaused = true
        audioTrack?.pause()
    }

    fun resume() {
        if (!isActive || !isPaused) return
        isPaused = false
        audioTrack?.play()
    }

    /** Immediate stop — no fade, slight click acceptable (emergency / lifecycle). */
    fun stop() {
        fadingOut = false
        isActive = false
        // Stop the track to unblock any in-progress write()
        audioTrack?.stop()
        try { playbackThread?.join(1000) } catch (_: InterruptedException) {}
        playbackThread = null
        releaseTrack()
        resetGenerators()
    }

    /** Graceful stop — 500 ms fade out, then releases AudioTrack on audio thread. */
    fun fadeOutAndStop() {
        if (isActive) fadingOut = true
    }

    fun setFrequencies(left: Double, right: Double) {
        freqLeft = left
        freqRight = right
    }

    fun setVolume(vol: Float) {
        amplitude = vol.coerceIn(0f, 1f)
    }

    // ── Audio thread ──────────────────────────────────────────────────────────

    private fun runLoop() {
        val buffer = FloatArray(AppConstants.BUFFER_FRAMES * 2) // interleaved stereo

        while (isActive) {
            if (isPaused) {
                Thread.sleep(20)
                continue
            }

            // Snapshot volatile params once per buffer to stay consistent
            val fl  = freqLeft
            val fr  = freqRight
            val amp = amplitude
            val nt  = noiseType
            val nv  = noiseVolume

            // Generate normalised binaural stereo buffer (amplitude = 1.0)
            val raw = sineGen.generateStereoBuffer(fl, fr, 1.0f, AppConstants.BUFFER_FRAMES)

            var envelopeHitZero = false
            for (i in 0 until AppConstants.BUFFER_FRAMES) {
                // Ramp envelope toward target
                envelope = if (fadingOut) {
                    (envelope - fadeStep).coerceAtLeast(0f)
                } else {
                    (envelope + fadeStep).coerceAtMost(1f)
                }

                val envAmp = amp * envelope

                val noise = when (nt) {
                    NoiseType.WHITE -> noiseGen.whiteNoiseSample() * nv
                    NoiseType.PINK  -> noiseGen.pinkNoiseSample()  * nv
                    NoiseType.BROWN -> noiseGen.brownNoiseSample() * nv
                    NoiseType.NONE  -> 0f
                }

                buffer[i * 2]     = (raw[i * 2]     * envAmp + noise).coerceIn(-1f, 1f)
                buffer[i * 2 + 1] = (raw[i * 2 + 1] * envAmp + noise).coerceIn(-1f, 1f)

                if (fadingOut && envelope <= 0f) envelopeHitZero = true
            }

            // WRITE_BLOCKING: returns when the buffer has been accepted by the driver
            val written = audioTrack?.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING) ?: -1
            if (written < 0) break // track was stopped externally

            if (envelopeHitZero) {
                isActive = false
            }
        }

        // Fade-out path: thread owns cleanup. Immediate-stop path: stop() already released.
        if (fadingOut) {
            releaseTrack()
        }
        resetGenerators()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun releaseTrack() {
        val track = audioTrack ?: return
        audioTrack = null
        runCatching { track.stop() }
        track.release()
    }

    private fun resetGenerators() {
        sineGen.reset()
        noiseGen.reset()
        envelope = 0f
    }

    private fun buildAudioTrack(): AudioTrack {
        // Buffer must hold at least BUFFER_FRAMES stereo floats (4 bytes each)
        val minBytes = AudioTrack.getMinBufferSize(
            AppConstants.SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_FLOAT
        )
        val bufferBytes = maxOf(minBytes, AppConstants.BUFFER_FRAMES * 2 * Float.SIZE_BYTES)

        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(AppConstants.SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .build()
            )
            .setBufferSizeInBytes(bufferBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }
}
