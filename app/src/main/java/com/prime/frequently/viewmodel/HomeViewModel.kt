package com.prime.frequently.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.audio.BinauralPlayer
import com.prime.frequently.audio.NoiseType
import com.prime.frequently.data.SessionRecord
import com.prime.frequently.data.WavePreset
import com.prime.frequently.repository.SessionRepository
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
    object TimerCompleted : SessionEvent()
    object Stopped : SessionEvent()
}

// ── Timer lifecycle ───────────────────────────────────────────────────────────
enum class TimerState { IDLE, RUNNING, PAUSED, COMPLETED }

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val player = BinauralPlayer()
    private val sessionRepo = SessionRepository(app)

    // ── Current preset ────────────────────────────────────────────────────────
    private val _currentPresetName = MutableStateFlow("")
    val currentPresetName: StateFlow<String> = _currentPresetName.asStateFlow()

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

    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    val remainingFormatted: String
        get() = TimeUtils.secondsToMmSs(_remainingSeconds.value)

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    var sessionStartTime: Long = 0L
        private set

    // ── One-shot events ───────────────────────────────────────────────────────
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

    fun stop() {
        val record = buildSessionRecord(completed = false)
        player.fadeOutAndStop()
        _isPlaying.value = false
        cancelTimer()
        viewModelScope.launch {
            if (record.actualDurationSecs > 0) sessionRepo.insert(record)
            _events.emit(SessionEvent.Stopped)
        }
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

    // ── Preset ────────────────────────────────────────────────────────────────

    fun applyPreset(preset: WavePreset) {
        _carrierHz.value = preset.carrierHz
        _beatHz.value = preset.beatHz
        _currentPresetName.value = preset.name
        player.noiseType = preset.noiseType
        player.noiseVolume = preset.noiseVolume
        if (_isPlaying.value) player.setFrequencies(freqLeft, freqRight)
    }

    // ── Timer controls ────────────────────────────────────────────────────────

    fun setDurationMinutes(minutes: Int) {
        _durationSeconds.value = minutes * 60
        _remainingSeconds.value = minutes * 60
    }

    fun setDurationSeconds(seconds: Int) {
        _durationSeconds.value = seconds
        _remainingSeconds.value = seconds
    }

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
        val record = buildSessionRecord(completed = true)
        player.fadeOutAndStop()
        _isPlaying.value = false
        _timerState.value = TimerState.COMPLETED
        viewModelScope.launch {
            sessionRepo.insert(record)
            _events.emit(SessionEvent.TimerCompleted)
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value = TimerState.IDLE
        _remainingSeconds.value = _durationSeconds.value
        _elapsedSeconds.value = 0
    }

    private fun buildSessionRecord(completed: Boolean): SessionRecord {
        val actualDuration = if (_durationSeconds.value > 0) {
            _elapsedSeconds.value
        } else {
            if (sessionStartTime > 0) ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt() else 0
        }
        return SessionRecord(
            startTime = sessionStartTime,
            plannedDurationSecs = _durationSeconds.value,
            actualDurationSecs = actualDuration,
            presetName = _currentPresetName.value.ifEmpty { "Custom" },
            carrierHz = _carrierHz.value,
            beatHz = _beatHz.value,
            noiseType = player.noiseType.name,
            noiseVolume = player.noiseVolume,
            completed = completed
        )
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }
}
