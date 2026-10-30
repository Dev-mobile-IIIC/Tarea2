package com.example.myfi.domain.model

import java.time.LocalDate

data class SavingsGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: LocalDate? = null,
    val priority: GoalPriority
)

enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH
}