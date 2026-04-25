package com.prime.frequently.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.audio.AudioEngine
import com.prime.frequently.audio.NoiseType
import com.prime.frequently.data.FrequencyJourney
import com.prime.frequently.data.SessionIntent
import com.prime.frequently.data.SessionRecord
import com.prime.frequently.data.WavePreset
import com.prime.frequently.repository.IntentRecommendationEngine
import com.prime.frequently.repository.SessionRepository
import com.prime.frequently.service.AudioForegroundService
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

sealed class SessionEvent {
    object TimerCompleted : SessionEvent()
    object Stopped : SessionEvent()
}

enum class TimerState { IDLE, RUNNING, PAUSED, COMPLETED }

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val sessionRepo = SessionRepository(app)

    // ── Service binding ────────────────────────────────────────────────────────
    private var svc: AudioForegroundService? = null
    private var isBound = false
    private var pendingPlay = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            svc = (service as AudioForegroundService.LocalBinder).service
            isBound = true
            svc!!.callback = object : AudioForegroundService.PlaybackCallback {
                override fun onNotificationPause() = onExternalPause()
                override fun onNotificationResume() {
                    _isPlaying.value = true
                    if (_timerState.value == TimerState.PAUSED) _timerState.value = TimerState.RUNNING
                }
                override fun onNotificationStop() = onExternalStop()
                override fun onAudioFocusLoss() = onExternalStop()
                override fun onAudioFocusLossTransient() = onExternalPause()
                override fun onAudioFocusGain() {
                    if (_timerState.value == TimerState.PAUSED) {
                        _isPlaying.value = true
                        _timerState.value = TimerState.RUNNING
                    }
                }
                override fun onHeadphonesUnplugged() = onExternalPause()
            }
            if (pendingPlay) {
                pendingPlay = false
                doPlay()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            svc = null
            isBound = false
        }
    }

    init {
        getApplication<Application>().bindService(
            Intent(getApplication(), AudioForegroundService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    // ── Current preset ─────────────────────────────────────────────────────────
    private val _currentPresetName = MutableStateFlow("")
    val currentPresetName: StateFlow<String> = _currentPresetName.asStateFlow()

    // ── Playback ───────────────────────────────────────────────────────────────
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // ── Frequency ──────────────────────────────────────────────────────────────
    private val _carrierHz = MutableStateFlow(200.0)
    val carrierHz: StateFlow<Double> = _carrierHz.asStateFlow()

    private val _beatHz = MutableStateFlow(10.0)
    val beatHz: StateFlow<Double> = _beatHz.asStateFlow()

    val freqLeft: Double get() = FrequencyUtils.leftHz(_carrierHz.value, _beatHz.value)
    val freqRight: Double get() = FrequencyUtils.rightHz(_carrierHz.value, _beatHz.value)

    // ── Volume ─────────────────────────────────────────────────────────────────
    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // ── Noise (ViewModel owns these values; applies them to service player) ────
    private var noiseType = NoiseType.NONE
    private var noiseVolume = 0f

    // ── Timer ──────────────────────────────────────────────────────────────────
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

    // ── One-shot events ────────────────────────────────────────────────────────
    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    // ── Journey (Phase 11.2) ───────────────────────────────────────────────────
    private val _activeJourney = MutableStateFlow<FrequencyJourney?>(null)
    val activeJourney: StateFlow<FrequencyJourney?> = _activeJourney.asStateFlow()

    private var journeyJob: Job? = null
    private var lastNotifRefreshElapsed = 0L

    // ── Playback controls ──────────────────────────────────────────────────────

    fun play() {
        if (svc == null) { pendingPlay = true; return }
        doPlay()
    }

    private fun doPlay() {
        val s = svc ?: return

        s.notifPresetName = _currentPresetName.value.ifEmpty { "Custom" }
        s.notifBeatHz = _beatHz.value
        s.notifPlaying = true

        getApplication<Application>().startForegroundService(
            Intent(getApplication(), AudioForegroundService::class.java).apply {
                putExtra(AudioForegroundService.EXTRA_PRESET_NAME, s.notifPresetName)
                putExtra(AudioForegroundService.EXTRA_BEAT_HZ, s.notifBeatHz)
            }
        )

        if (!s.requestAudioFocus()) return

        s.player.setFrequencies(freqLeft, freqRight)
        s.player.setVolume(_volume.value)
        s.player.noiseType = noiseType
        s.player.noiseVolume = noiseVolume
        s.player.start()

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
        svc?.player?.pause()
        svc?.notifPlaying = false
        svc?.refreshNotification()
        _isPlaying.value = false
        if (_timerState.value == TimerState.RUNNING) {
            _timerState.value = TimerState.PAUSED
        }
    }

    fun resume() {
        svc?.player?.resume()
        svc?.notifPlaying = true
        svc?.refreshNotification()
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
        svc?.player?.fadeOutAndStop()
        svc?.cleanup()
        _isPlaying.value = false
        cancelTimer()
        cancelJourney()
        viewModelScope.launch {
            if (record.actualDurationSecs > 0) sessionRepo.insert(record)
            _events.emit(SessionEvent.Stopped)
        }
    }

    // ── Frequency ──────────────────────────────────────────────────────────────

    fun setCarrierHz(hz: Double) {
        _carrierHz.value = hz
        if (_isPlaying.value) svc?.player?.setFrequencies(freqLeft, freqRight)
    }

    fun setBeatHz(hz: Double) {
        _beatHz.value = hz
        if (_isPlaying.value) svc?.player?.setFrequencies(freqLeft, freqRight)
    }

    // ── Volume ─────────────────────────────────────────────────────────────────

    fun setVolume(vol: Float) {
        _volume.value = vol
        svc?.player?.setVolume(vol)
    }

    // ── Noise ──────────────────────────────────────────────────────────────────

    fun setNoiseType(type: NoiseType) {
        noiseType = type
        svc?.player?.noiseType = type
    }

    fun setNoiseVolume(vol: Float) {
        noiseVolume = vol
        svc?.player?.noiseVolume = vol
    }

    // ── Preset ─────────────────────────────────────────────────────────────────

    fun applyPreset(preset: WavePreset) {
        if (preset.journey != null) {
            startJourney(preset.journey, preset.name)
            return
        }
        cancelJourney()
        _carrierHz.value = preset.carrierHz
        _beatHz.value = preset.beatHz
        _currentPresetName.value = preset.name
        noiseType = preset.noiseType
        noiseVolume = preset.noiseVolume
        svc?.player?.noiseType = preset.noiseType
        svc?.player?.noiseVolume = preset.noiseVolume
        if (_isPlaying.value) {
            svc?.player?.setFrequencies(freqLeft, freqRight)
            svc?.notifPresetName = preset.name
            svc?.notifBeatHz = preset.beatHz
            svc?.refreshNotification()
        }
    }

    // ── Journey (Phase 11.2) ───────────────────────────────────────────────────

    fun startJourney(journey: FrequencyJourney, name: String) {
        cancelJourney()

        // Stop current audio cleanly before starting journey
        if (_isPlaying.value) {
            svc?.player?.stop()
            _isPlaying.value = false
            cancelTimer()
        }

        _activeJourney.value = journey
        _currentPresetName.value = name
        lastNotifRefreshElapsed = 0L

        val firstWp = journey.waypoints.firstOrNull()
        _carrierHz.value = firstWp?.carrierHz ?: 200.0
        _beatHz.value    = firstWp?.beatHz    ?: 10.0
        noiseType   = firstWp?.noiseType   ?: NoiseType.NONE
        noiseVolume = firstWp?.noiseVolume ?: 0f

        if (journey.totalDurationMinutes > 0) {
            _durationSeconds.value  = journey.totalDurationMinutes * 60
            _remainingSeconds.value = journey.totalDurationMinutes * 60
        }

        play()
        launchJourneyTick(journey)
    }

    private fun launchJourneyTick(journey: FrequencyJourney) {
        journeyJob?.cancel()
        journeyJob = viewModelScope.launch {
            while (_activeJourney.value != null) {
                if (_isPlaying.value) {
                    val elapsed = _elapsedSeconds.value.toLong()
                    val (carrier, beat) = AudioEngine.computeHz(elapsed, journey.waypoints)

                    _carrierHz.value = carrier
                    _beatHz.value    = beat
                    svc?.player?.setFrequencies(
                        FrequencyUtils.leftHz(carrier, beat),
                        FrequencyUtils.rightHz(carrier, beat)
                    )

                    // Switch noise type at waypoint boundaries
                    val (nType, nVol) = AudioEngine.computeNoiseState(elapsed, journey.waypoints)
                    if (nType != noiseType || nVol != noiseVolume) {
                        noiseType   = nType
                        noiseVolume = nVol
                        svc?.player?.noiseType   = nType
                        svc?.player?.noiseVolume = nVol
                    }

                    // Refresh notification every 60 s
                    if (elapsed - lastNotifRefreshElapsed >= 60) {
                        lastNotifRefreshElapsed = elapsed
                        svc?.notifBeatHz = beat
                        svc?.refreshNotification()
                    }
                }
                delay(500L)
            }
        }
    }

    private fun cancelJourney() {
        journeyJob?.cancel()
        journeyJob = null
        _activeJourney.value = null
    }

    // ── Session Intent (Phase 11.1) ────────────────────────────────────────────

    fun applyIntent(intent: SessionIntent) {
        val rec = IntentRecommendationEngine.recommend(intent)
        if (rec.journey != null) {
            startJourney(rec.journey, intent.label)
            return
        }
        cancelJourney()
        _currentPresetName.value = intent.label
        _carrierHz.value = rec.carrierHz
        _beatHz.value    = rec.beatHz
        noiseType   = rec.noiseType
        noiseVolume = rec.noiseVolume
        if (rec.durationMinutes > 0) setDurationMinutes(rec.durationMinutes)
        if (_isPlaying.value) {
            svc?.player?.setFrequencies(freqLeft, freqRight)
            svc?.player?.noiseType   = rec.noiseType
            svc?.player?.noiseVolume = rec.noiseVolume
        } else {
            play()
        }
    }

    // ── Timer controls ─────────────────────────────────────────────────────────

    fun setDurationMinutes(minutes: Int) {
        _durationSeconds.value  = minutes * 60
        _remainingSeconds.value = minutes * 60
    }

    fun setDurationSeconds(seconds: Int) {
        _durationSeconds.value  = seconds
        _remainingSeconds.value = seconds
    }

    fun clearDuration() {
        _durationSeconds.value  = 0
        _remainingSeconds.value = 0
        cancelTimer()
    }

    // ── Internal timer ─────────────────────────────────────────────────────────

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
        svc?.player?.fadeOutAndStop()
        svc?.cleanup()
        _isPlaying.value = false
        _timerState.value = TimerState.COMPLETED
        cancelJourney()
        viewModelScope.launch {
            sessionRepo.insert(record)
            _events.emit(SessionEvent.TimerCompleted)
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value   = TimerState.IDLE
        _remainingSeconds.value = _durationSeconds.value
        _elapsedSeconds.value   = 0
    }

    // ── External event handlers (fired by service callback) ───────────────────

    private fun onExternalPause() {
        _isPlaying.value = false
        if (_timerState.value == TimerState.RUNNING) {
            _timerState.value = TimerState.PAUSED
        }
    }

    private fun onExternalStop() {
        val record = buildSessionRecord(completed = false)
        _isPlaying.value = false
        cancelTimer()
        cancelJourney()
        viewModelScope.launch {
            if (record.actualDurationSecs > 0) sessionRepo.insert(record)
            _events.emit(SessionEvent.Stopped)
        }
    }

    // ── Session record ─────────────────────────────────────────────────────────

    private fun buildSessionRecord(completed: Boolean): SessionRecord {
        val actualDuration = if (_durationSeconds.value > 0) {
            _elapsedSeconds.value
        } else {
            if (sessionStartTime > 0) ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt() else 0
        }
        return SessionRecord(
            startTime            = sessionStartTime,
            plannedDurationSecs  = _durationSeconds.value,
            actualDurationSecs   = actualDuration,
            presetName           = _currentPresetName.value.ifEmpty { "Custom" },
            carrierHz            = _carrierHz.value,
            beatHz               = _beatHz.value,
            noiseType            = noiseType.name,
            noiseVolume          = noiseVolume,
            completed            = completed
        )
    }

    override fun onCleared() {
        super.onCleared()
        cancelJourney()
        svc?.callback = null
        svc?.cleanup()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
