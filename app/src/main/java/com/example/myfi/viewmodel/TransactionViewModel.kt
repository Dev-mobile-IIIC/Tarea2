package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.Transaction
import com.example.myfi.domain.repository.DataRepository

class TransactionViewModel(
    private val repository: DataRepository
) : ViewModel() {

    fun getTransactions(): List<Transaction> = repository.getTransactions()

    fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }

    fun deleteTransaction(transactionId: String) {
        repository.deleteTransaction(transactionId)
    }
}
