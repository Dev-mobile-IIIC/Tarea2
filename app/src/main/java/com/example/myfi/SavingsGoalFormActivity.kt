package com.example.myfi

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.myfi.databinding.ActivitySavingsGoalFormBinding
import com.example.myfi.domain.model.GoalPriority
import com.example.myfi.domain.model.SavingsGoal
import com.example.myfi.viewmodel.RepositoryViewModelFactory
import com.example.myfi.viewmodel.SavingsGoalViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class SavingsGoalFormActivity : AppCompatActivity() {
    companion object { const val EXTRA_GOAL_ID = "goal_id" }
    private lateinit var binding: ActivitySavingsGoalFormBinding
    private val repository by lazy { (application as MyFiApplication).repository }
    private val viewModel by lazy { ViewModelProvider(this, RepositoryViewModelFactory(repository))[SavingsGoalViewModel::class.java] }
    private var goalId: String? = null
    private var targetDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingsGoalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goalId = intent.getStringExtra(EXTRA_GOAL_ID)
        goalId?.let { load(viewModel.getSavingsGoalById(it)) }
        binding.btnSelectGoalDate.setOnClickListener { chooseDate() }
        binding.btnSaveGoal.setOnClickListener { save() }
        binding.btnCancelGoal.setOnClickListener { finish() }
        updateDate()
    }

    private fun load(goal: SavingsGoal?) {
        if (goal == null) { finish(); return }
        binding.etGoalName.setText(goal.name)
        binding.etGoalTargetAmount.setText(goal.targetAmount.toString())
        targetDate = goal.targetDate
        binding.cbConsiderEssentialExpenses.isChecked = goal.considerEssentialExpenses
        binding.rgGoalPriority.check(when (goal.priority) {
            GoalPriority.HIGH -> R.id.rbPriorityHigh
            GoalPriority.MEDIUM -> R.id.rbPriorityMedium
            GoalPriority.LOW -> R.id.rbPriorityLow
        })
    }

    private fun chooseDate() {
        val initial = targetDate ?: LocalDate.now()
        val calendar = Calendar.getInstance().apply { time = java.util.Date.from(initial.atStartOfDay(ZoneId.systemDefault()).toInstant()) }
        DatePickerDialog(this, { _, y, m, d -> targetDate = LocalDate.of(y, m + 1, d); updateDate() },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDate() { binding.tvGoalSelectedDate.text = targetDate?.toString().orEmpty() }

    private fun save() {
        val name = binding.etGoalName.text?.toString()?.trim().orEmpty()
        val amount = binding.etGoalTargetAmount.text?.toString()?.trim()?.replace(',', '.')?.toDoubleOrNull()
        if (name.isBlank()) { binding.tilGoalName.error = getString(R.string.required_field); return }
        binding.tilGoalName.error = null
        if (amount == null || amount <= 0.0) { binding.tilGoalTargetAmount.error = getString(R.string.invalid_amount); return }
        binding.tilGoalTargetAmount.error = null
        val priority = when (binding.rgGoalPriority.checkedRadioButtonId) {
            R.id.rbPriorityHigh -> GoalPriority.HIGH
            R.id.rbPriorityLow -> GoalPriority.LOW
            else -> GoalPriority.MEDIUM
        }
        val current = goalId?.let(viewModel::getSavingsGoalById)?.currentAmount ?: 0.0
        val goal = SavingsGoal(goalId ?: com.example.myfi.util.IdGenerator.newId(), name, amount, current, targetDate, priority, binding.cbConsiderEssentialExpenses.isChecked)
        if (goalId == null) viewModel.addSavingsGoal(goal) else viewModel.updateSavingsGoal(goal)
        finish()
    }
}
