package com.example.myfi.data.memory

import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.domain.model.Transaction
import com.example.myfi.domain.repository.DataRepository
import java.time.LocalDate

class MemoryDataRepository : DataRepository {
    private val categories = mutableListOf<Category>()
    private val transactions = mutableListOf<Transaction>()
    private val savingsGoals = mutableListOf<SavingsGoal>()

    init {
        categories += listOf(
            Category("default_income_salary", "Salary", CategoryType.INCOME, true),
            Category("default_income_other", "Other income", CategoryType.INCOME, true),
            Category("default_expense_food", "Food", CategoryType.EXPENSE, true),
            Category("default_expense_transport", "Transport", CategoryType.EXPENSE, true),
            Category("default_expense_housing", "Housing", CategoryType.EXPENSE, true),
            Category("default_expense_health", "Health", CategoryType.EXPENSE, true),
            Category("default_expense_other", "Other expense", CategoryType.EXPENSE, true)
        )
    }

    override fun getCategories(): List<Category> = categories.toList()
    override fun getCategoryById(categoryId: String): Category? = categories.firstOrNull { it.id == categoryId }

    override fun addCategory(category: Category) {
        require(!category.isDefault) { "Custom categories cannot be marked as default." }
        require(categories.none { it.id == category.id })
        categories.add(category)
    }

    override fun updateCategory(category: Category) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index >= 0 && !categories[index].isDefault && !category.isDefault) categories[index] = category
    }

    override fun deleteCategory(categoryId: String) {
        val category = getCategoryById(categoryId) ?: return
        if (!category.isDefault && getCategoryUsageCount(categoryId) == 0) categories.remove(category)
    }

    override fun getTransactions(): List<Transaction> = transactions.toList()
    override fun getTransactionById(transactionId: String): Transaction? =
        transactions.firstOrNull { it.id == transactionId }

    override fun getTransactions(
        startDate: LocalDate?,
        endDate: LocalDate?,
        type: CategoryType?,
        categoryId: String?
    ): List<Transaction> = transactions.filter { transaction ->
        (startDate == null || !transaction.date.isBefore(startDate)) &&
            (endDate == null || !transaction.date.isAfter(endDate)) &&
            (type == null || getCategoryType(transaction.categoryId) == type) &&
            (categoryId == null || transaction.categoryId == categoryId)
    }.sortedByDescending { it.date }

    override fun addTransaction(transaction: Transaction) {
        require(transaction.amount > 0.0)
        require(getCategoryById(transaction.categoryId) != null)
        transactions.add(transaction)
    }

    override fun updateTransaction(transaction: Transaction) {
        require(transaction.amount > 0.0)
        require(getCategoryById(transaction.categoryId) != null)
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index >= 0) transactions[index] = transaction
    }

    override fun deleteTransaction(transactionId: String) {
        transactions.removeAll { it.id == transactionId }
    }

    override fun getSavingsGoals(): List<SavingsGoal> = savingsGoals.toList()
    override fun getSavingsGoalById(goalId: String): SavingsGoal? = savingsGoals.firstOrNull { it.id == goalId }

    override fun addSavingsGoal(goal: SavingsGoal) {
        require(goal.targetAmount > 0.0)
        savingsGoals.add(goal)
    }

    override fun updateSavingsGoal(goal: SavingsGoal) {
        require(goal.targetAmount > 0.0)
        val index = savingsGoals.indexOfFirst { it.id == goal.id }
        if (index >= 0) savingsGoals[index] = goal
    }

    override fun deleteSavingsGoal(goalId: String) {
        savingsGoals.removeAll { it.id == goalId }
    }

    override fun getCategoryType(categoryId: String): CategoryType? = getCategoryById(categoryId)?.type
    override fun getCategoryUsageCount(categoryId: String): Int = transactions.count { it.categoryId == categoryId }

    override fun getIncomeTotal(startDate: LocalDate?, endDate: LocalDate?): Double =
        getTransactions(startDate, endDate, CategoryType.INCOME, null).sumOf { it.amount }

    override fun getExpenseTotal(startDate: LocalDate?, endDate: LocalDate?): Double =
        getTransactions(startDate, endDate, CategoryType.EXPENSE, null).sumOf { it.amount }
}
