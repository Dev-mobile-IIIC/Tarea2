package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.repository.DataRepository

class SavingsGoalViewModel(
    private val repository: DataRepository
) : ViewModel() {

    fun getSavingsGoals(): List<SavingsGoal> = repository.getSavingsGoals()

    fun addSavingsGoal(goal: SavingsGoal) {
        repository.addSavingsGoal(goal)
    }

    fun updateSavingsGoal(goal: SavingsGoal) {
        repository.updateSavingsGoal(goal)
    }

    fun deleteSavingsGoal(goalId: String) {
        repository.deleteSavingsGoal(goalId)
    }
}
