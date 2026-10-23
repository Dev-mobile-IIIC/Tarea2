package com.example.myfi.domain.repository

import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.model.Transaction

interface DataRepository {
    fun getCategories(): List<Category>
    fun addCategory(category: Category)
    fun updateCategory(category: Category)
    fun deleteCategory(categoryId: String)

    fun getTransactions(): List<Transaction>
    fun addTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun deleteTransaction(transactionId: String)

    fun getSavingsGoals(): List<SavingsGoal>
    fun addSavingsGoal(goal: SavingsGoal)
    fun updateSavingsGoal(goal: SavingsGoal)
    fun deleteSavingsGoal(goalId: String)
}