package com.example.myfi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfi.databinding.ItemTransactionBinding
import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.model.Transaction
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionAdapter(
    private var items: List<Transaction>,
    private var categories: Map<String, Category>,
    private val onClick: (Transaction) -> Unit,
    private val onLongClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    fun submitList(newItems: List<Transaction>, newCategories: Map<String, Category> = categories) {
        items = newItems
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder =
        TransactionViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) =
        holder.bind(items[position], categories, onClick, onLongClick)

    override fun getItemCount(): Int = items.size

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction, categories: Map<String, Category>, onClick: (Transaction) -> Unit, onLongClick: (Transaction) -> Unit) {
            val category = categories[item.categoryId]
            binding.tvTransactionTitle.text = item.note?.takeIf(String::isNotBlank) ?: category?.name.orEmpty()
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
            binding.tvTransactionCategoryAndDate.text = listOfNotNull(category?.name, item.date.format(formatter)).joinToString(" • ")
            binding.tvTransactionAmount.text = NumberFormat.getCurrencyInstance().format(item.amount)
            binding.tvTransactionType.text = when (category?.type) {
                CategoryType.INCOME -> binding.root.context.getString(com.example.myfi.R.string.income)
                CategoryType.EXPENSE -> binding.root.context.getString(com.example.myfi.R.string.expense)
                null -> ""
            }
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener { onLongClick(item); true }
        }
    }
}
