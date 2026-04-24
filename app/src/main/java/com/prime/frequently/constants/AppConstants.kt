package com.prime.frequently.constants

object AppConstants {
    const val SAMPLE_RATE = 44100
    const val BUFFER_FRAMES = 4096
    const val FADE_DURATION_MS = 500
    const val MIN_SESSION_SECS_FOR_STREAK = 300   // 5 min minimum counts toward streak

    const val CARRIER_HZ_MIN = 100.0
    const val CARRIER_HZ_MAX = 500.0
    const val BEAT_HZ_MIN = 0.5
    const val BEAT_HZ_MAX = 100.0

    const val NOTIFICATION_CHANNEL_ID = "binaural_playback"
    const val NOTIFICATION_ID = 1001

    const val DB_NAME = "frequently.db"

    // Timer preset chips shown in UI (Phase 3 / 7)
    val TIMER_PRESET_MINUTES = listOf(5, 10, 20, 30, 45, 60, 90)

    // Preference keys — must match android:key in res/xml/preferences.xml
    const val PREF_KEEP_SCREEN_ON    = "pref_keep_screen_on"
    const val PREF_HEADPHONE_WARNING = "pref_headphone_warning"
    const val PREF_CARRIER_HZ        = "pref_carrier_hz"
    const val PREF_NOISE_TYPE        = "pref_noise_type"
    const val PREF_SAMPLE_RATE       = "pref_sample_rate"
    const val PREF_AUTO_FADE         = "pref_auto_fade"
    const val PREF_MAX_VOL           = "pref_max_vol"
    const val PREF_EXPORT            = "pref_export"
    const val PREF_CLEAR_DATA        = "pref_clear_data"
}
