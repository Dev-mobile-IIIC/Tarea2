package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.model.Transaction
import com.example.myfi.domain.repository.DataRepository
import com.example.myfi.util.IdGenerator
import java.time.LocalDate

class TransactionViewModel(private val repository: DataRepository) : ViewModel() {
    fun getTransactions(): List<Transaction> = repository.getTransactions().sortedByDescending { it.date }
    fun getTransactionById(transactionId: String): Transaction? = repository.getTransactionById(transactionId)
    fun getTransactions(start: LocalDate?, end: LocalDate?, type: CategoryType?, categoryId: String?): List<Transaction> =
        repository.getTransactions(start, end, type, categoryId)
    fun getCategoryType(categoryId: String): CategoryType? = repository.getCategoryType(categoryId)
    fun getCategoryName(categoryId: String): String? = repository.getCategoryById(categoryId)?.name
    fun getIncomeTotal(start: LocalDate? = null, end: LocalDate? = null): Double = repository.getIncomeTotal(start, end)
    fun getExpenseTotal(start: LocalDate? = null, end: LocalDate? = null): Double = repository.getExpenseTotal(start, end)
    fun addTransaction(transaction: Transaction) = repository.addTransaction(transaction)
    fun updateTransaction(transaction: Transaction) = repository.updateTransaction(transaction)
    fun deleteTransaction(transactionId: String) = repository.deleteTransaction(transactionId)
    fun createTransaction(amount: Double, categoryId: String, date: LocalDate, note: String?, receiptUri: String?, latitude: Double?, longitude: Double?, locationName: String?, locationAddress: String?): Transaction {
        require(amount > 0.0 && repository.getCategoryById(categoryId) != null)
        val transaction = Transaction(IdGenerator.newId(), amount, categoryId, date, note?.takeIf { it.isNotBlank() }, receiptUri, latitude, longitude, locationName, locationAddress)
        repository.addTransaction(transaction)
        return transaction
    }
}
