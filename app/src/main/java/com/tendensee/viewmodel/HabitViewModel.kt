package com.tendensee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tendensee.data.AppDatabase
import com.tendensee.data.Habit
import com.tendensee.data.HabitRecord
import com.tendensee.data.HabitRepository
import com.tendensee.data.SchedulingType
import com.tendensee.data.GoalType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository

    val allHabits: StateFlow<List<Habit>>

    init {
        val habitDao = AppDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(habitDao)
        allHabits = repository.allHabits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addHabit(
        title: String,
        description: String = "",
        color: Int = 0xFF4CAF50.toInt(),
        schedulingType: SchedulingType = SchedulingType.DAILY,
        frequency: Int = 1,
        daysOfWeek: String = "",
        goalType: GoalType = GoalType.AT_LEAST,
        goalTarget: Float = 1.0f
    ) {
        viewModelScope.launch {
            repository.addHabit(
                title, 
                description,
                color,
                schedulingType,
                frequency,
                daysOfWeek,
                goalType,
                goalTarget
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleHabitCompletion(
        habitId: Int,
        date: LocalDate,
        isCompleted: Boolean,
        value: Float = 1.0f,
        note: String? = null
    ) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, date, isCompleted, value, note)
        }
    }
    
    fun getRecentRecords(days: Int): StateFlow<List<HabitRecord>> {
        val today = LocalDate.now()
        val start = today.minusDays(days.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return repository.getRecordsInRange(start, end).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun getRecordsForDate(date: LocalDate): StateFlow<List<HabitRecord>> {
        return repository.getRecordsForDate(date).stateIn(
             scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun getAllRecordsForHabit(habitId: Int): StateFlow<List<HabitRecord>> {
        return repository.getAllRecordsForHabit(habitId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    suspend fun getHabitById(habitId: Int): Habit? {
        return repository.getHabitById(habitId)
    }
}