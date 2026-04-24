package com.prime.frequently.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prime.frequently.data.SessionRecord
import com.prime.frequently.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HistoryStats(
    val totalSessions: Int,
    val totalMinutes: Int,
    val streakDays: Int,
    val weekCounts: List<Int>  // 7 values, index 0 = oldest of last 7 days
)

class HistoryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SessionRepository(app)

    val sessions: StateFlow<List<SessionRecord>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stats: StateFlow<HistoryStats> = sessions.map { list ->
        HistoryStats(
            totalSessions = list.size,
            totalMinutes = list.sumOf { it.actualDurationSecs } / 60,
            streakDays = computeStreak(list),
            weekCounts = computeWeekCounts(list)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HistoryStats(0, 0, 0, List(7) { 0 })
    )

    fun delete(id: String) = viewModelScope.launch { repo.deleteById(id) }

    fun deleteAll() = viewModelScope.launch { repo.deleteAll() }

    private fun computeStreak(sessions: List<SessionRecord>): Int {
        val qualifyingDays = sessions
            .filter { it.actualDurationSecs >= 300 }
            .map { dayKey(it.startTime) }
            .toSortedSet()
            .toList()
            .reversed()

        if (qualifyingDays.isEmpty()) return 0

        val today = dayKey(System.currentTimeMillis())
        var streak = 0
        var expected = today

        for (day in qualifyingDays) {
            if (day == expected) {
                streak++
                expected -= DAY_MILLIS
            } else if (day < expected) break
        }
        return streak
    }

    private fun computeWeekCounts(sessions: List<SessionRecord>): List<Int> {
        val now = System.currentTimeMillis()
        val today = dayKey(now)
        return (6 downTo 0).map { daysAgo ->
            val dayStart = today - daysAgo * DAY_MILLIS
            val dayEnd = dayStart + DAY_MILLIS
            sessions.count { it.startTime in dayStart until dayEnd }
        }
    }

    private fun dayKey(epochMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = epochMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 86_400_000L
    }
}
