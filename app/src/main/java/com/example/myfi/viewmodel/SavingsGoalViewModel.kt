package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.repository.DataRepository
import com.example.myfi.util.IdGenerator

class SavingsGoalViewModel(private val repository: DataRepository) : ViewModel() {
    fun getSavingsGoals(): List<SavingsGoal> = repository.getSavingsGoals()
    fun getSavingsGoalById(goalId: String): SavingsGoal? = repository.getSavingsGoalById(goalId)
    fun getPriorityGoal(): SavingsGoal? = repository.getSavingsGoals()
        .filter { it.progress < 1f }
        .sortedWith(compareByDescending<SavingsGoal> { it.priority.ordinal }.thenBy { it.targetDate })
        .firstOrNull()
    fun getTotalSaved(): Double = repository.getSavingsGoals().sumOf { it.currentAmount }
    fun getTotalTarget(): Double = repository.getSavingsGoals().sumOf { it.targetAmount }
    fun addSavingsGoal(goal: SavingsGoal) = repository.addSavingsGoal(goal)
    fun createSavingsGoal(name: String, targetAmount: Double, targetDate: java.time.LocalDate?, priority: com.example.myfi.domain.model.GoalPriority, considerEssentialExpenses: Boolean): SavingsGoal {
        val goal = SavingsGoal(IdGenerator.newId(), name.trim(), targetAmount, targetDate = targetDate, priority = priority, considerEssentialExpenses = considerEssentialExpenses)
        require(goal.name.isNotBlank() && goal.targetAmount > 0.0)
        repository.addSavingsGoal(goal)
        return goal
    }
    fun updateSavingsGoal(goal: SavingsGoal) = repository.updateSavingsGoal(goal)
    fun deleteSavingsGoal(goalId: String) = repository.deleteSavingsGoal(goalId)
}
