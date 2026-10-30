package com.example.myfi.domain.model

import java.time.LocalDate

data class Transaction(
    val id: String,
    val amount: Double,
    val categoryId: String,
    val date: LocalDate,
    val note: String? = null,
    val receiptUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    val locationAddress: String? = null
)
