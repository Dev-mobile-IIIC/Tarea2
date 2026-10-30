package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfi.domain.repository.DataRepository

class RepositoryViewModelFactory(
    private val repository: DataRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> TransactionViewModel(repository)
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(repository)
            modelClass.isAssignableFrom(SavingsGoalViewModel::class.java) -> SavingsGoalViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return viewModel as T
    }
}
