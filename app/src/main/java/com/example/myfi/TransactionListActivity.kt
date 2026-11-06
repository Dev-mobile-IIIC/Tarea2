package com.example.myfi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfi.databinding.ActivityTransactionListBinding
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.domain.model.Transaction
import com.example.myfi.ui.AppNavigation
import com.example.myfi.ui.TransactionAdapter
import com.example.myfi.viewmodel.RepositoryViewModelFactory
import com.example.myfi.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.YearMonth

class TransactionListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionListBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val viewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[TransactionViewModel::class.java] }
    private var selectedType: CategoryType? = null
    private var selectedCategoryId: String? = null
    private var periodIndex = 0
    private val periodLabels by lazy { listOf(getString(R.string.all_time), getString(R.string.this_month), getString(R.string.last_month), getString(R.string.this_year)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppNavigation.bindBottomNavigation(this, binding.bottomNavigation)
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = TransactionAdapter(emptyList(), emptyMap(), { openForm(it) }, { confirmDelete(it) })
        val labels = listOf(getString(R.string.all), getString(R.string.income), getString(R.string.expense))
        binding.chipAll.text = labels[0]
        binding.chipIncome.text = labels[1]
        binding.chipExpense.text = labels[2]
        binding.chipAll.setOnClickListener { selectedType = null; selectedCategoryId = null; refresh() }
        binding.chipIncome.setOnClickListener { selectedType = CategoryType.INCOME; selectedCategoryId = null; refresh() }
        binding.chipExpense.setOnClickListener { selectedType = CategoryType.EXPENSE; selectedCategoryId = null; refresh() }
        binding.actvPeriod.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, periodLabels))
        binding.actvPeriod.setText(periodLabels[periodIndex], false)
        binding.actvPeriod.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            periodIndex = position
            refresh()
        }
        binding.fabAddTransaction.setOnClickListener { openForm(null) }
        binding.tvEmptyTransactions.setText(R.string.no_transactions)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) refresh()
    }

    private fun refresh() {
        val range = periodRange(periodIndex)
        val income = viewModel.getIncomeTotal(range.first, range.second)
        val expense = viewModel.getExpenseTotal(range.first, range.second)
        binding.tvTransactionIncomeTotal.text = java.text.NumberFormat.getCurrencyInstance().format(income)
        binding.tvTransactionExpenseTotal.text = java.text.NumberFormat.getCurrencyInstance().format(expense)
        val items = viewModel.getTransactions(range.first, range.second, selectedType, selectedCategoryId)
        val cats = repository.getCategories()
        (binding.rvTransactions.adapter as TransactionAdapter).submitList(items, cats.associateBy { it.id })
        binding.tvEmptyTransactions.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTransactions.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        binding.chipGroupCategories.removeAllViews()
        cats.filter { selectedType == null || it.type == selectedType }.forEach { category ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = category.name
                isCheckable = true
                isChecked = category.id == selectedCategoryId
                setOnClickListener { selectedCategoryId = if (isChecked) category.id else null; refresh() }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun periodRange(index: Int): Pair<LocalDate?, LocalDate?> = when (index) {
        1 -> YearMonth.now().let { it.atDay(1) to it.atEndOfMonth() }
        2 -> YearMonth.now().minusMonths(1).let { it.atDay(1) to it.atEndOfMonth() }
        3 -> LocalDate.now().withDayOfYear(1) to LocalDate.now()
        else -> null to null
    }

    private fun openForm(transaction: Transaction?) {
        startActivity(Intent(this, TransactionFormActivity::class.java).apply {
            transaction?.let { putExtra(TransactionFormActivity.EXTRA_TRANSACTION_ID, it.id) }
        })
    }

    private fun confirmDelete(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteTransaction(transaction.id); refresh() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
