package com.example.myfi

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfi.databinding.ActivityMainBinding
import com.example.myfi.domain.model.Category
import com.example.myfi.domain.model.CategoryType
import com.example.myfi.ui.AppNavigation
import com.example.myfi.ui.TransactionAdapter
import com.example.myfi.viewmodel.RepositoryViewModelFactory
import com.example.myfi.viewmodel.SavingsGoalViewModel
import com.example.myfi.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val transactionViewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[TransactionViewModel::class.java] }
    private val goalViewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[SavingsGoalViewModel::class.java] }
    private val currency by lazy { NumberFormat.getCurrencyInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppNavigation.bindBottomNavigation(this, binding.bottomNavigation)
        binding.tvGreeting.setText(R.string.greeting)
        binding.tvGreetingSubtitle.setText(R.string.greeting_subtitle)
        binding.tvSelectedPeriod.setText(R.string.selected_period_month)
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentTransactions.adapter = TransactionAdapter(emptyList(), emptyMap(), { openTransaction(it.id) }, {})
        refresh()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) refresh()
    }

    private fun refresh() {
        val now = LocalDate.now()
        val start = YearMonth.from(now).atDay(1)
        val end = YearMonth.from(now).atEndOfMonth()
        val income = transactionViewModel.getIncomeTotal(start, end)
        val expense = transactionViewModel.getExpenseTotal(start, end)
        binding.tvAvailableBalance.text = currency.format(income - expense)
        binding.tvIncomeTotal.text = currency.format(income)
        binding.tvExpenseTotal.text = currency.format(expense)
        val monthlyExpenses = transactionViewModel.getTransactions(null, null, CategoryType.EXPENSE, null)
            .groupBy { YearMonth.from(it.date) }
            .values.map { month -> month.sumOf { it.amount } }
        val averageMonthlyExpense = monthlyExpenses.average().takeIf { !it.isNaN() } ?: 0.0
        binding.tvMonthlyExpenseAmount.text = currency.format(expense)
        binding.tvMonthlyExpenseLimit.text = getString(R.string.monthly_expense_limit, currency.format(averageMonthlyExpense))
        binding.progressMonthlyExpenses.max = 100
        binding.progressMonthlyExpenses.progress = if (averageMonthlyExpense > 0.0) ((expense / averageMonthlyExpense) * 100).toInt().coerceIn(0, 100) else 0
        binding.tvRemainingBudget.setText(R.string.budget_unset)
        val priority = goalViewModel.getPriorityGoal()
        binding.tvPriorityGoalName.text = priority?.name.orEmpty()
        binding.tvPriorityGoalAmounts.text = priority?.let {
            getString(R.string.goal_progress, currency.format(it.currentAmount), currency.format(it.targetAmount))
        }.orEmpty()
        binding.progressPriorityGoal.max = 100
        binding.progressPriorityGoal.progress = ((priority?.progress ?: 0f) * 100).toInt()
        val categories = repository.getCategories().associateBy(Category::id)
        val recent = transactionViewModel.getTransactions().take(5)
        (binding.rvRecentTransactions.adapter as TransactionAdapter).submitList(recent, categories)
        binding.tvEmptyRecentTransactions.visibility = if (recent.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvRecentTransactions.visibility = if (recent.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvEmptyRecentTransactions.setText(R.string.no_transactions)
    }

    private fun openTransaction(id: String) {
        startActivity(Intent(this, TransactionFormActivity::class.java).putExtra(TransactionFormActivity.EXTRA_TRANSACTION_ID, id))
    }
}
