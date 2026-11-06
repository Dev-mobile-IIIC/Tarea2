package com.example.myfi

import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfi.databinding.ActivityCategoriesBinding
import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.ui.AppNavigation
import com.example.myfi.ui.CategoryAdapter
import com.example.myfi.viewmodel.CategoryViewModel
import com.example.myfi.viewmodel.RepositoryViewModelFactory

class CategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriesBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val viewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[CategoryViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppNavigation.bindBottomNavigation(this, binding.bottomNavigation)
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = CategoryAdapter(emptyList(), emptyMap(), ::editCategory, ::deleteCategory)
        binding.fabAddCategory.setOnClickListener { chooseCategoryType() }
        binding.tvEmptyCategories.setText(R.string.no_categories)
        refresh()
    }

    override fun onResume() { super.onResume(); if (::binding.isInitialized) refresh() }

    private fun refresh() {
        val custom = viewModel.getCategories().filterNot { it.isDefault }
        val usages = custom.associate { it.id to viewModel.getUsageCount(it.id) }
        (binding.rvCategories.adapter as CategoryAdapter).submitList(custom, usages)
        binding.tvEmptyCategories.visibility = if (custom.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvCategories.visibility = if (custom.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun chooseCategoryType() {
        AlertDialog.Builder(this).setTitle(R.string.select_category_type)
            .setItems(arrayOf(getString(R.string.category_type_income), getString(R.string.category_type_expense))) { _, which ->
                showCategoryDialog(null, if (which == 0) CategoryType.INCOME else CategoryType.EXPENSE)
            }.show()
    }

    private fun editCategory(category: Category) {
        if (!category.isDefault) showCategoryDialog(category, category.type)
    }

    private fun showCategoryDialog(existing: Category?, type: CategoryType) {
        val input = EditText(this).apply {
            setSingleLine(true)
            hint = getString(R.string.category_name)
            setText(existing?.name.orEmpty())
        }
        AlertDialog.Builder(this)
            .setTitle(if (existing == null) R.string.add_category else R.string.edit_category)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    if (existing == null) viewModel.addCategory(name, type)
                    else viewModel.updateCategory(existing.copy(name = name))
                    refresh()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteCategory(category: Category) {
        if (viewModel.getUsageCount(category.id) > 0) return
        AlertDialog.Builder(this).setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteCategory(category.id); refresh() }
            .setNegativeButton(R.string.cancel, null).show()
    }
}
