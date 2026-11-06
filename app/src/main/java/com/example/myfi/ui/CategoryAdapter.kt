package com.example.myfi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfi.databinding.ItemCategoryBinding
import com.example.myfi.domain.model.Category

class CategoryAdapter(
    private var items: List<Category>,
    private var usageCounts: Map<String, Int>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    fun submitList(newItems: List<Category>, newUsageCounts: Map<String, Int> = usageCounts) {
        items = newItems
        usageCounts = newUsageCounts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) =
        holder.bind(items[position], usageCounts, onEdit, onDelete)

    override fun getItemCount(): Int = items.size

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, usageCounts: Map<String, Int>, onEdit: (Category) -> Unit, onDelete: (Category) -> Unit) {
            binding.tvCategoryInitial.text = category.name.take(1).uppercase()
            binding.tvCategoryName.text = category.name
            binding.tvCategoryUsageCount.text = binding.root.context.getString(
                com.example.myfi.R.string.category_usage_count,
                usageCounts[category.id] ?: 0
            )
            binding.btnEditCategory.isEnabled = !category.isDefault
            binding.btnDeleteCategory.isEnabled = !category.isDefault && (usageCounts[category.id] ?: 0) == 0
            binding.btnEditCategory.setOnClickListener { onEdit(category) }
            binding.btnDeleteCategory.setOnClickListener { onDelete(category) }
        }
    }
}
