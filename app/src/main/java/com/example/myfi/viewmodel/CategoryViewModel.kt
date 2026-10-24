package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.Category
import com.example.myfi.domain.repository.DataRepository

class CategoryViewModel(
    private val repository: DataRepository
) : ViewModel() {

    fun getCategories(): List<Category> = repository.getCategories()

    fun addCategory(category: Category) {
        repository.addCategory(category)
    }

    fun updateCategory(category: Category) {
        repository.updateCategory(category)
    }

    fun deleteCategory(categoryId: String) {
        repository.deleteCategory(categoryId)
    }
}
