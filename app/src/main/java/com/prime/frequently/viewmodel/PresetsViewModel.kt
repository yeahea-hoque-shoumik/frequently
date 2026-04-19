package com.prime.frequently.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.data.WaveCategory
import com.prime.frequently.data.WavePreset
import com.prime.frequently.repository.PresetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PresetsViewModel : ViewModel() {

    private val repo = PresetRepository()

    private val _selectedCategory = MutableStateFlow<WaveCategory?>(null)
    val selectedCategory: StateFlow<WaveCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val presets: StateFlow<List<WavePreset>> = combine(_selectedCategory, _searchQuery) { cat, query ->
        var list = if (cat == null) repo.getAll() else repo.getByCategory(cat)
        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.Eagerly, repo.getAll())

    fun selectCategory(category: WaveCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
