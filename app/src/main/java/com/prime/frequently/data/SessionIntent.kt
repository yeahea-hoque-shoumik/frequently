package com.prime.frequently.data

enum class SessionIntent(val label: String, val description: String, val emoji: String) {
    DEEP_SLEEP(
        label = "Deep Sleep",
        description = "Delta waves for healing rest and deep sleep",
        emoji = "🌙"  // 🌙
    ),
    MEDITATION(
        label = "Meditation",
        description = "Theta waves for inner stillness and awareness",
        emoji = "🧘"  // 🧘
    ),
    STUDY(
        label = "Study & Focus",
        description = "Beta waves for sharp, sustained concentration",
        emoji = "📚"  // 📚
    ),
    CREATIVE(
        label = "Creative Flow",
        description = "Theta–Alpha for imaginative, associative thinking",
        emoji = "✨"         // ✨
    ),
    FLOW_STATE(
        label = "Flow State",
        description = "Beta → Alpha → Theta progressive journey (75 min)",
        emoji = "🌊"  // 🌊
    ),
    ANXIETY_RELIEF(
        label = "Anxiety Relief",
        description = "Alpha waves to release tension and calm the mind",
        emoji = "💚"  // 💚
    ),
    WIND_DOWN(
        label = "Wind Down",
        description = "Alpha → Theta → Delta sleep preparation (40 min)",
        emoji = "😴"  // 😴
    )
}
