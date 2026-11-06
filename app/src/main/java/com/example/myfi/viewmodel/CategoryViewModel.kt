package com.example.myfi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.repository.DataRepository
import com.example.myfi.util.IdGenerator

class CategoryViewModel(private val repository: DataRepository) : ViewModel() {
    fun getCategories(): List<Category> = repository.getCategories()
    fun getCategories(type: CategoryType): List<Category> = repository.getCategories().filter { it.type == type }
    fun getCategoryById(categoryId: String): Category? = repository.getCategoryById(categoryId)
    fun getUsageCount(categoryId: String): Int = repository.getCategoryUsageCount(categoryId)

    fun addCategory(name: String, type: CategoryType): Category {
        val category = Category(IdGenerator.newId(), name.trim(), type, false)
        require(category.name.isNotBlank())
        repository.addCategory(category)
        return category
    }

    fun addCategory(category: Category) = repository.addCategory(category)
    fun updateCategory(category: Category) = repository.updateCategory(category)
    fun deleteCategory(categoryId: String) = repository.deleteCategory(categoryId)
}
