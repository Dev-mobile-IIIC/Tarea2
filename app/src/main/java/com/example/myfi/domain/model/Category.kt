package com.example.myfi.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val isDefault: Boolean = false
)

enum class CategoryType {
    INCOME,
    EXPENSE
}
