package com.example.myfi.domain.repository

import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.model.Transaction
import com.example.myfi.domain.model.CategoryType
import java.time.LocalDate

interface DataRepository {
    fun getCategories(): List<Category>
    fun getCategoryById(categoryId: String): Category?
    fun addCategory(category: Category)
    fun updateCategory(category: Category)
    fun deleteCategory(categoryId: String)

    fun getTransactions(): List<Transaction>
    fun getTransactionById(transactionId: String): Transaction?
    fun getTransactions(startDate: LocalDate?, endDate: LocalDate?, type: CategoryType?, categoryId: String?): List<Transaction>
    fun addTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun deleteTransaction(transactionId: String)

    fun getSavingsGoals(): List<SavingsGoal>
    fun getSavingsGoalById(goalId: String): SavingsGoal?
    fun addSavingsGoal(goal: SavingsGoal)
    fun updateSavingsGoal(goal: SavingsGoal)
    fun deleteSavingsGoal(goalId: String)

    fun getCategoryType(categoryId: String): CategoryType?
    fun getCategoryUsageCount(categoryId: String): Int
    fun getIncomeTotal(startDate: LocalDate? = null, endDate: LocalDate? = null): Double
    fun getExpenseTotal(startDate: LocalDate? = null, endDate: LocalDate? = null): Double
}
