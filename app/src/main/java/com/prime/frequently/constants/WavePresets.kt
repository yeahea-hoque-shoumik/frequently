package com.prime.frequently.constants

import com.prime.frequently.audio.NoiseType
import com.prime.frequently.data.JourneyPresets
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.data.WavePreset

// Full preset library — referenced by PresetRepository and IntentRecommendationEngine
object WavePresets {

    // Delta (0.5–4 Hz) — sleep, healing
    private val DEEP_SLEEP = WavePreset(
        id = "delta_deep_sleep", name = "Deep Sleep",
        category = WaveCategory.DELTA, carrierHz = 150.0, beatHz = 2.0,
        description = "Promote deep restorative sleep",
        noiseType = NoiseType.BROWN, noiseVolume = 0.4f, recommendedDurationMin = 45
    )
    private val HEALING_REST = WavePreset(
        id = "delta_healing_rest", name = "Healing Rest",
        category = WaveCategory.DELTA, carrierHz = 150.0, beatHz = 1.0,
        description = "Deep healing and cellular restoration",
        noiseType = NoiseType.BROWN, noiseVolume = 0.3f, recommendedDurationMin = 30
    )

    // Theta (4–8 Hz) — meditation, creativity
    private val DEEP_MEDITATION = WavePreset(
        id = "theta_deep_meditation", name = "Deep Meditation",
        category = WaveCategory.THETA, carrierHz = 200.0, beatHz = 5.0,
        description = "Reach deep meditative states", recommendedDurationMin = 30
    )
    private val CREATIVE_FLOW = WavePreset(
        id = "theta_creative_flow", name = "Creative Flow",
        category = WaveCategory.THETA, carrierHz = 200.0, beatHz = 6.0,
        description = "Unlock creative insight and imagination", recommendedDurationMin = 20
    )
    private val INTUITION = WavePreset(
        id = "theta_intuition", name = "Intuition",
        category = WaveCategory.THETA, carrierHz = 200.0, beatHz = 7.0,
        description = "Heighten intuitive awareness", recommendedDurationMin = 20
    )

    // Alpha (8–13 Hz) — calm focus, relaxation
    private val CALM_AWARENESS = WavePreset(
        id = "alpha_calm_awareness", name = "Calm Awareness",
        category = WaveCategory.ALPHA, carrierHz = 200.0, beatHz = 10.0,
        description = "Relaxed, alert presence", recommendedDurationMin = 20
    )
    private val RELAXED_FOCUS = WavePreset(
        id = "alpha_relaxed_focus", name = "Relaxed Focus",
        category = WaveCategory.ALPHA, carrierHz = 200.0, beatHz = 11.0,
        description = "Calm and ready to work", recommendedDurationMin = 20
    )
    private val DREAMY_STATE = WavePreset(
        id = "alpha_dreamy", name = "Dreamy State",
        category = WaveCategory.ALPHA, carrierHz = 200.0, beatHz = 8.0,
        description = "Gentle, dream-like relaxation", recommendedDurationMin = 15
    )

    // Beta (13–30 Hz) — active focus, study
    private val STUDY_MODE = WavePreset(
        id = "beta_study", name = "Study Mode",
        category = WaveCategory.BETA, carrierHz = 300.0, beatHz = 15.0,
        description = "Sustained focus for deep work",
        noiseType = NoiseType.PINK, noiseVolume = 0.3f, recommendedDurationMin = 90
    )
    private val ACTIVE_THINKING = WavePreset(
        id = "beta_active", name = "Active Thinking",
        category = WaveCategory.BETA, carrierHz = 300.0, beatHz = 20.0,
        description = "Sharp analytical thinking", recommendedDurationMin = 45
    )

    // Gamma (30–100 Hz) — peak cognition
    private val PEAK_PERFORMANCE = WavePreset(
        id = "gamma_peak", name = "Peak Performance",
        category = WaveCategory.GAMMA, carrierHz = 400.0, beatHz = 40.0,
        description = "Maximum cognitive performance", recommendedDurationMin = 20
    )
    private val INSIGHT = WavePreset(
        id = "gamma_insight", name = "Insight",
        category = WaveCategory.GAMMA, carrierHz = 400.0, beatHz = 35.0,
        description = "Heightened perception and insight", recommendedDurationMin = 20
    )

    // Spiritual — prayer-aligned (Phase 11.4)
    private val PRE_SALAH_CALM = WavePreset(
        id = "spiritual_pre_salah", name = "Pre-Salah Calm",
        nameArabic = "تهيئة قبل الصلاة",
        category = WaveCategory.SPIRITUAL, carrierHz = 200.0, beatHz = 10.0,
        description = "Settle mind before prayer", recommendedDurationMin = 5
    )
    private val POST_SALAH_EXTENSION = WavePreset(
        id = "spiritual_post_salah", name = "Post-Salah Extension",
        nameArabic = "امتداد ما بعد الصلاة",
        category = WaveCategory.SPIRITUAL, carrierHz = 200.0, beatHz = 6.0,
        description = "Extend the state of khushu",
        noiseType = NoiseType.PINK, noiseVolume = 0.2f, recommendedDurationMin = 10
    )
    private val TAHAJJUD_PREP = WavePreset(
        id = "spiritual_tahajjud", name = "Tahajjud Prep",
        nameArabic = "التهيئة للتهجد",
        category = WaveCategory.SPIRITUAL, carrierHz = 150.0, beatHz = 4.0,
        description = "Ease waking for night prayer",
        noiseType = NoiseType.BROWN, noiseVolume = 0.4f, recommendedDurationMin = 15
    )
    private val QURAN_MEMORIZATION = WavePreset(
        id = "spiritual_quran", name = "Quran Memorization",
        nameArabic = "حفظ القرآن",
        category = WaveCategory.SPIRITUAL, carrierHz = 200.0, beatHz = 7.0,
        description = "Receptive state for memorization", recommendedDurationMin = 30
    )
    private val RAMADAN_FOCUS = WavePreset(
        id = "spiritual_ramadan", name = "Ramadan Focus",
        nameArabic = "تركيز رمضان",
        category = WaveCategory.SPIRITUAL, carrierHz = 200.0, beatHz = 8.0,
        description = "Fasting-adapted calm focus", recommendedDurationMin = 20
    )
    private val DHIKR_DEEPENING = WavePreset(
        id = "spiritual_dhikr", name = "Dhikr Deepening",
        nameArabic = "تعميق الذكر",
        category = WaveCategory.SPIRITUAL, carrierHz = 200.0, beatHz = 5.0,
        description = "Deepen the state of remembrance", recommendedDurationMin = 15
    )

    // Journeys (Phase 11.2) — progressive multi-phase sessions
    private val JOURNEY_FLOW_STATE = WavePreset(
        id = "journey_flow_state",
        name = "Flow State",
        category = WaveCategory.JOURNEY,
        carrierHz = 300.0, beatHz = 18.0,
        description = "Beta → Alpha → Theta progressive journey for peak creative flow",
        recommendedDurationMin = 75,
        journey = JourneyPresets.FLOW_STATE
    )
    private val JOURNEY_WIND_DOWN = WavePreset(
        id = "journey_wind_down",
        name = "Wind Down",
        category = WaveCategory.JOURNEY,
        carrierHz = 200.0, beatHz = 10.0,
        description = "Alpha → Theta → Delta sleep preparation with gentle noise fade",
        noiseType = NoiseType.PINK, noiseVolume = 0.3f,
        recommendedDurationMin = 40,
        journey = JourneyPresets.WIND_DOWN
    )
    private val JOURNEY_DEEP_MEDITATION = WavePreset(
        id = "journey_deep_meditation",
        name = "Deep Meditation",
        category = WaveCategory.JOURNEY,
        carrierHz = 200.0, beatHz = 10.0,
        description = "Alpha → Theta journey for deep meditative states",
        recommendedDurationMin = 45,
        journey = JourneyPresets.DEEP_MEDITATION
    )
    private val JOURNEY_STUDY = WavePreset(
        id = "journey_study",
        name = "Study Session",
        category = WaveCategory.JOURNEY,
        carrierHz = 200.0, beatHz = 10.0,
        description = "Alpha warm-up → sustained Beta focus with pink noise",
        noiseType = NoiseType.PINK, noiseVolume = 0.3f,
        recommendedDurationMin = 90,
        journey = JourneyPresets.STUDY_SESSION
    )

    val ALL: List<WavePreset> = listOf(
        JOURNEY_FLOW_STATE, JOURNEY_WIND_DOWN, JOURNEY_DEEP_MEDITATION, JOURNEY_STUDY,
        DEEP_SLEEP, HEALING_REST,
        DEEP_MEDITATION, CREATIVE_FLOW, INTUITION,
        CALM_AWARENESS, RELAXED_FOCUS, DREAMY_STATE,
        STUDY_MODE, ACTIVE_THINKING,
        PEAK_PERFORMANCE, INSIGHT,
        PRE_SALAH_CALM, POST_SALAH_EXTENSION, TAHAJJUD_PREP,
        QURAN_MEMORIZATION, RAMADAN_FOCUS, DHIKR_DEEPENING
    )
}
