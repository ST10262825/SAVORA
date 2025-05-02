package vcmsa.projects.savorabudgetapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private val onClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    private var expenses = emptyList<Expense>()

    inner class ViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<Expense>) {
        expenses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.apply {
            tvCategory.text = expense.Categoryname  // Fixed: Access Expense property
            tvAmount.text = "R${"%.2f".format(expense.amount)}"  // Formatted amount
            root.setOnClickListener { onClick(expense) }
        }
    }

    override fun getItemCount() = expenses.size
}