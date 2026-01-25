package com.tendensee.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SchedulingType {
    DAILY,
    WEEKLY,
    SPECIFIC_DAYS
}

enum class GoalType {
    AT_LEAST,
    EXACTLY,
    AT_MOST
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val color: Int,
    val schedulingType: SchedulingType,
    val frequency: Int, // e.g., 3 for 3 times a week
    val daysOfWeek: String, // e.g., "1,3,5" for Mon, Wed, Fri
    val goalType: GoalType,
    val goalTarget: Float, // e.g., 30.0 for 30 minutes
    val createdAt: Long,
    val lastModified: Long,
    val isArchived: Boolean = false
)
