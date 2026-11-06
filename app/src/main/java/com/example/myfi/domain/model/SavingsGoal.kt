package com.example.myfi.domain.model

import java.time.LocalDate

data class SavingsGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: LocalDate? = null,
    val priority: GoalPriority,
    val considerEssentialExpenses: Boolean = false
) {
    val progress: Float
        get() = if (targetAmount <= 0.0) 0f else (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()
}

enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH
}
