package com.example.myfi.data.memory

import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.model.Transaction
import com.example.myfi.domain.repository.DataRepository

class MemoryDataRepository : DataRepository {
    private val categories = mutableListOf<Category>()
    private val transactions = mutableListOf<Transaction>()
    private val savingsGoals = mutableListOf<SavingsGoal>()

    override fun getCategories(): List<Category> = categories.toList()

    override fun addCategory(category: Category) {
        categories.add(category)
    }

    override fun updateCategory(category: Category) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) categories[index] = category
    }

    override fun deleteCategory(categoryId: String) {
        categories.removeAll { it.id == categoryId }
    }

    override fun getTransactions(): List<Transaction> = transactions.toList()

    override fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    override fun updateTransaction(transaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) transactions[index] = transaction
    }

    override fun deleteTransaction(transactionId: String) {
        transactions.removeAll { it.id == transactionId }
    }

    override fun getSavingsGoals(): List<SavingsGoal> = savingsGoals.toList()

    override fun addSavingsGoal(goal: SavingsGoal) {
        savingsGoals.add(goal)
    }

    override fun updateSavingsGoal(goal: SavingsGoal) {
        val index = savingsGoals.indexOfFirst { it.id == goal.id }
        if (index != -1) savingsGoals[index] = goal
    }

    override fun deleteSavingsGoal(goalId: String) {
        savingsGoals.removeAll { it.id == goalId }
    }
}