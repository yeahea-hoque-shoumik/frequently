package com.prime.frequently.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.audio.BinauralPlayer
import com.prime.frequently.audio.NoiseType
import com.prime.frequently.utils.FrequencyUtils
import com.prime.frequently.utils.TimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── One-shot UI events ────────────────────────────────────────────────────────
sealed class SessionEvent {
    /** Timer counted down to zero — show completion UI. */
    object TimerCompleted : SessionEvent()
    /** User manually stopped the session. */
    object Stopped : SessionEvent()
}

// ── Timer lifecycle ───────────────────────────────────────────────────────────
enum class TimerState { IDLE, RUNNING, PAUSED, COMPLETED }

class HomeViewModel : ViewModel() {

    private val player = BinauralPlayer()

    // ── Playback ──────────────────────────────────────────────────────────────
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // ── Frequency ─────────────────────────────────────────────────────────────
    private val _carrierHz = MutableStateFlow(200.0)
    val carrierHz: StateFlow<Double> = _carrierHz.asStateFlow()

    private val _beatHz = MutableStateFlow(10.0)
    val beatHz: StateFlow<Double> = _beatHz.asStateFlow()

    val freqLeft: Double get() = FrequencyUtils.leftHz(_carrierHz.value, _beatHz.value)
    val freqRight: Double get() = FrequencyUtils.rightHz(_carrierHz.value, _beatHz.value)

    // ── Volume ────────────────────────────────────────────────────────────────
    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // ── Timer ─────────────────────────────────────────────────────────────────
    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    /** Chosen session duration in seconds. 0 = infinite (no auto-stop). */
    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    /** Counts down from durationSeconds to 0. */
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    /** MM:SS string derived from remainingSeconds — ready to bind to a TextView. */
    val remainingFormatted: String
        get() = TimeUtils.secondsToMmSs(_remainingSeconds.value)

    /** Seconds elapsed since session started (for session record in Phase 6). */
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    /** Wall-clock epoch millis when the current session started (Phase 6). */
    var sessionStartTime: Long = 0L
        private set

    // ── One-shot events → UI (SharedFlow, no replay) ─────────────────────────
    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    // ── Playback controls ─────────────────────────────────────────────────────

    fun play() {
        player.setFrequencies(freqLeft, freqRight)
        player.setVolume(_volume.value)
        player.start()
        _isPlaying.value = true
        sessionStartTime = System.currentTimeMillis()
        _elapsedSeconds.value = 0

        if (_durationSeconds.value > 0) {
            _remainingSeconds.value = _durationSeconds.value
            _timerState.value = TimerState.RUNNING
            launchTimerCoroutine()
        }
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
        if (_timerState.value == TimerState.RUNNING) {
            _timerState.value = TimerState.PAUSED
        }
    }

    fun resume() {
        player.resume()
        _isPlaying.value = true
        if (_timerState.value == TimerState.PAUSED) {
            _timerState.value = TimerState.RUNNING
        }
    }

    fun togglePlayPause() {
        when {
            _isPlaying.value -> pause()
            _timerState.value == TimerState.PAUSED -> resume()
            else -> play()
        }
    }

    /** Manual stop — fades audio out and resets timer. */
    fun stop() {
        player.fadeOutAndStop()
        _isPlaying.value = false
        cancelTimer()
        viewModelScope.launch { _events.emit(SessionEvent.Stopped) }
    }

    // ── Frequency ─────────────────────────────────────────────────────────────

    fun setCarrierHz(hz: Double) {
        _carrierHz.value = hz
        if (_isPlaying.value) player.setFrequencies(freqLeft, freqRight)
    }

    fun setBeatHz(hz: Double) {
        _beatHz.value = hz
        if (_isPlaying.value) player.setFrequencies(freqLeft, freqRight)
    }

    // ── Volume ────────────────────────────────────────────────────────────────

    fun setVolume(vol: Float) {
        _volume.value = vol
        player.setVolume(vol)
    }

    // ── Noise ─────────────────────────────────────────────────────────────────

    fun setNoiseType(type: NoiseType) { player.noiseType = type }
    fun setNoiseVolume(vol: Float) { player.noiseVolume = vol }

    // ── Timer controls ────────────────────────────────────────────────────────

    /** Set duration from a preset chip (minutes → seconds). */
    fun setDurationMinutes(minutes: Int) {
        _durationSeconds.value = minutes * 60
        _remainingSeconds.value = minutes * 60
    }

    /** Set duration as seconds directly (e.g. from a slider). */
    fun setDurationSeconds(seconds: Int) {
        _durationSeconds.value = seconds
        _remainingSeconds.value = seconds
    }

    /** Clear the timer — playback becomes infinite until manual stop. */
    fun clearDuration() {
        _durationSeconds.value = 0
        _remainingSeconds.value = 0
        cancelTimer()
    }

    // ── Internal timer machinery ──────────────────────────────────────────────

    private fun launchTimerCoroutine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                // Only decrement if not paused — if paused, spin in place
                if (_timerState.value == TimerState.RUNNING) {
                    _remainingSeconds.value = (_remainingSeconds.value - 1).coerceAtLeast(0)
                    _elapsedSeconds.value += 1
                }
            }
            if (_timerState.value != TimerState.IDLE) {
                onTimerComplete()
            }
        }
    }

    private fun onTimerComplete() {
        player.fadeOutAndStop()
        _isPlaying.value = false
        _timerState.value = TimerState.COMPLETED
        viewModelScope.launch { _events.emit(SessionEvent.TimerCompleted) }
        // Phase 6: save session record here
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value = TimerState.IDLE
        _remainingSeconds.value = _durationSeconds.value // reset display
        _elapsedSeconds.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }
}
