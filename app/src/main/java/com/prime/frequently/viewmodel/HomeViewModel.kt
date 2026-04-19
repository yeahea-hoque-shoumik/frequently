package com.prime.frequently.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.audio.BinauralPlayer
import com.prime.frequently.audio.NoiseType
import com.prime.frequently.utils.FrequencyUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val player = BinauralPlayer()

    // ── Playback state ────────────────────────────────────────────────────────
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // ── Frequency state ───────────────────────────────────────────────────────
    private val _carrierHz = MutableStateFlow(200.0)
    val carrierHz: StateFlow<Double> = _carrierHz.asStateFlow()

    private val _beatHz = MutableStateFlow(10.0)
    val beatHz: StateFlow<Double> = _beatHz.asStateFlow()

    val freqLeft: Double get() = FrequencyUtils.leftHz(_carrierHz.value, _beatHz.value)
    val freqRight: Double get() = FrequencyUtils.rightHz(_carrierHz.value, _beatHz.value)

    // ── Volume ────────────────────────────────────────────────────────────────
    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // ── Phase 3: timer (wired in Phase 3) ─────────────────────────────────────
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // ── Controls ──────────────────────────────────────────────────────────────

    fun play() {
        player.setFrequencies(freqLeft, freqRight)
        player.setVolume(_volume.value)
        player.start()
        _isPlaying.value = true
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
    }

    fun resume() {
        player.resume()
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else {
            if (player.isPlaying) resume() else play()
        }
    }

    fun stop() {
        player.fadeOutAndStop()
        _isPlaying.value = false
    }

    fun setCarrierHz(hz: Double) {
        _carrierHz.value = hz
        if (_isPlaying.value) player.setFrequencies(freqLeft, freqRight)
    }

    fun setBeatHz(hz: Double) {
        _beatHz.value = hz
        if (_isPlaying.value) player.setFrequencies(freqLeft, freqRight)
    }

    fun setVolume(vol: Float) {
        _volume.value = vol
        player.setVolume(vol)
    }

    fun setNoiseType(type: NoiseType) {
        player.noiseType = type
    }

    fun setNoiseVolume(vol: Float) {
        player.noiseVolume = vol
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }
}
