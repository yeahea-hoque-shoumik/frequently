package com.prime.frequently.constants

object AppConstants {
    const val SAMPLE_RATE = 44100
    const val BUFFER_FRAMES = 4096
    const val FADE_DURATION_MS = 500
    const val MIN_SESSION_SECS_FOR_STREAK = 300   // 5 min minimum counts toward streak

    const val CARRIER_HZ_MIN = 50.0
    const val CARRIER_HZ_MAX = 1000.0
    const val BEAT_HZ_MIN = 0.5
    const val BEAT_HZ_MAX = 100.0

    const val NOTIFICATION_CHANNEL_ID = "binaural_playback"
    const val NOTIFICATION_ID = 1001

    const val DB_NAME = "frequently.db"
}
