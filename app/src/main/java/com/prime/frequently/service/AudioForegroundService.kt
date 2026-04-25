package com.prime.frequently.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.prime.frequently.R
import com.prime.frequently.audio.BinauralPlayer
import com.prime.frequently.constants.AppConstants
import com.prime.frequently.ui.MainActivity

class AudioForegroundService : Service() {

    // ── Binder ─────────────────────────────────────────────────────────────────
    inner class LocalBinder : Binder() {
        val service: AudioForegroundService get() = this@AudioForegroundService
    }

    private val localBinder = LocalBinder()

    // ── Player (owned by service so audio survives Activity backgrounding) ─────
    val player = BinauralPlayer()

    // ── Notification display state (kept in sync by ViewModel) ────────────────
    var notifPresetName: String = "Binaural Beat"
    var notifBeatHz: Double = 10.0
    var notifPlaying: Boolean = false

    // ── ViewModel callback — fires on externally-triggered state changes ───────
    var callback: PlaybackCallback? = null

    interface PlaybackCallback {
        fun onNotificationPause()
        fun onNotificationResume()
        fun onNotificationStop()
        fun onAudioFocusLoss()
        fun onAudioFocusLossTransient()
        fun onAudioFocusGain()
        fun onHeadphonesUnplugged()
    }

    // ── Audio focus ────────────────────────────────────────────────────────────
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                player.stop()
                notifPlaying = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                callback?.onAudioFocusLoss()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (player.isPlaying) {
                    player.pause()
                    notifPlaying = false
                    refreshNotification()
                    callback?.onAudioFocusLossTransient()
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!player.isPlaying) {
                    player.resume()
                    notifPlaying = true
                    refreshNotification()
                    callback?.onAudioFocusGain()
                }
            }
        }
    }

    // ── Headphone unplug ───────────────────────────────────────────────────────
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (player.isPlaying) {
                player.pause()
                notifPlaying = false
                refreshNotification()
                callback?.onHeadphonesUnplugged()
            }
        }
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setWillPauseWhenDucked(true)
            .setOnAudioFocusChangeListener(focusListener)
            .build()
        registerReceiver(
            noisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    override fun onBind(intent: Intent?): IBinder = localBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> {
                if (player.isPlaying) {
                    player.pause()
                    notifPlaying = false
                    refreshNotification()
                    callback?.onNotificationPause()
                } else {
                    player.resume()
                    notifPlaying = true
                    refreshNotification()
                    callback?.onNotificationResume()
                }
            }
            ACTION_STOP -> {
                player.fadeOutAndStop()
                notifPlaying = false
                callback?.onNotificationStop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                // Triggered by ViewModel to promote to foreground and show notification.
                notifPresetName = intent?.getStringExtra(EXTRA_PRESET_NAME) ?: notifPresetName
                notifBeatHz = intent?.getDoubleExtra(EXTRA_BEAT_HZ, notifBeatHz) ?: notifBeatHz
                notifPlaying = true
                startForeground(AppConstants.NOTIFICATION_ID, buildNotification())
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        runCatching { unregisterReceiver(noisyReceiver) }
        if (::focusRequest.isInitialized) {
            runCatching { audioManager.abandonAudioFocusRequest(focusRequest) }
        }
    }

    // ── Public API (called via binder) ─────────────────────────────────────────

    /** Request audio focus before starting playback. Returns false if focus is denied. */
    fun requestAudioFocus(): Boolean =
        audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    fun refreshNotification() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(AppConstants.NOTIFICATION_ID, buildNotification())
    }

    /** Called by ViewModel on stop/clear — tears down audio + removes notification. */
    fun cleanup() {
        player.stop()
        runCatching { audioManager.abandonAudioFocusRequest(focusRequest) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // ── Notification ───────────────────────────────────────────────────────────

    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pauseOrResumeIntent = pendingServiceIntent(1, ACTION_PAUSE)
        val stopIntent = pendingServiceIntent(2, ACTION_STOP)

        return NotificationCompat.Builder(this, AppConstants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(notifPresetName)
            .setContentText("Δ %.1f Hz binaural beat".format(notifBeatHz))
            .setSmallIcon(R.drawable.ic_headphones)
            .setContentIntent(openIntent)
            .addAction(
                if (notifPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (notifPlaying) "Pause" else "Resume",
                pauseOrResumeIntent
            )
            .addAction(R.drawable.ic_close, "Stop", stopIntent)
            .setOngoing(notifPlaying)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun pendingServiceIntent(requestCode: Int, action: String): PendingIntent =
        PendingIntent.getService(
            this, requestCode,
            Intent(this, AudioForegroundService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            AppConstants.NOTIFICATION_CHANNEL_ID,
            "Binaural Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Controls for background binaural beat playback"
            setShowBadge(false)
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_PAUSE = "com.prime.frequently.ACTION_PAUSE"
        const val ACTION_STOP  = "com.prime.frequently.ACTION_STOP"
        const val EXTRA_PRESET_NAME = "extra_preset_name"
        const val EXTRA_BEAT_HZ     = "extra_beat_hz"
    }
}
