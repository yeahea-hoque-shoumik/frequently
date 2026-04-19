package com.prime.frequently.repository

import com.prime.frequently.data.WaveCategory
import com.prime.frequently.data.WavePreset
import com.prime.frequently.constants.WavePresets

// Phase 4: provides preset lists; wraps WavePresets constants for ViewModel consumption
class PresetRepository {
    fun getAll(): List<WavePreset> = WavePresets.ALL
    fun getByCategory(category: WaveCategory): List<WavePreset> =
        WavePresets.ALL.filter { it.category == category }
}
