package com.example.myfi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfi.databinding.ItemSavingsGoalBinding
import com.example.myfi.domain.model.SavingsGoal
import java.text.NumberFormat

class SavingsGoalAdapter(
    private var items: List<SavingsGoal>,
    private val onClick: (SavingsGoal) -> Unit,
    private val onLongClick: (SavingsGoal) -> Unit
) : RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {
    fun submitList(newItems: List<SavingsGoal>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder =
        GoalViewHolder(ItemSavingsGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) =
        holder.bind(items[position], onClick, onLongClick)

    override fun getItemCount(): Int = items.size

    class GoalViewHolder(private val binding: ItemSavingsGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: SavingsGoal, onClick: (SavingsGoal) -> Unit, onLongClick: (SavingsGoal) -> Unit) {
            binding.tvGoalPriority.text = binding.root.context.getString(
                when (goal.priority) {
                    com.example.myfi.domain.model.GoalPriority.HIGH -> com.example.myfi.R.string.high_priority
                    com.example.myfi.domain.model.GoalPriority.MEDIUM -> com.example.myfi.R.string.medium_priority
                    com.example.myfi.domain.model.GoalPriority.LOW -> com.example.myfi.R.string.low_priority
                }
            )
            binding.tvGoalName.text = goal.name
            binding.tvGoalProgressText.text = binding.root.context.getString(
                com.example.myfi.R.string.goal_progress,
                NumberFormat.getCurrencyInstance().format(goal.currentAmount),
                NumberFormat.getCurrencyInstance().format(goal.targetAmount)
            )
            binding.progressGoal.max = 100
            binding.progressGoal.progress = (goal.progress * 100).toInt()
            binding.root.setOnClickListener { onClick(goal) }
            binding.root.setOnLongClickListener { onLongClick(goal); true }
        }
    }
}
