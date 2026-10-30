package com.example.myfi

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfi.databinding.ActivitySavingsGoalsBinding
import com.example.myfi.ui.AppNavigation
import com.example.myfi.ui.SavingsGoalAdapter
import com.example.myfi.viewmodel.RepositoryViewModelFactory
import com.example.myfi.viewmodel.SavingsGoalViewModel
import java.text.NumberFormat

class SavingsGoalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavingsGoalsBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val viewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[SavingsGoalViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingsGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppNavigation.bindBottomNavigation(this, binding.bottomNavigation)
        binding.rvSavingsGoals.layoutManager = LinearLayoutManager(this)
        binding.rvSavingsGoals.adapter = SavingsGoalAdapter(emptyList(), { openForm(it.id) }, { confirmDelete(it.id) })
        binding.fabAddGoal.setOnClickListener { openForm(null) }
        binding.tvEmptyGoals.setText(R.string.no_goals)
        refresh()
    }

    override fun onResume() { super.onResume(); if (::binding.isInitialized) refresh() }

    private fun refresh() {
        val goals = viewModel.getSavingsGoals()
        binding.tvTotalSavings.text = NumberFormat.getCurrencyInstance().format(viewModel.getTotalSaved())
        binding.tvSavingsTarget.text = NumberFormat.getCurrencyInstance().format(viewModel.getTotalTarget())
        (binding.rvSavingsGoals.adapter as SavingsGoalAdapter).submitList(goals)
        binding.tvEmptyGoals.visibility = if (goals.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvSavingsGoals.visibility = if (goals.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun openForm(id: String?) = startActivity(Intent(this, SavingsGoalFormActivity::class.java).apply {
        id?.let { putExtra(SavingsGoalFormActivity.EXTRA_GOAL_ID, it) }
    })

    private fun confirmDelete(id: String) {
        AlertDialog.Builder(this).setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteSavingsGoal(id); refresh() }
            .setNegativeButton(R.string.cancel, null).show()
    }
}
