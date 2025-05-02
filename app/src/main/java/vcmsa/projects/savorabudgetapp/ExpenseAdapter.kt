package vcmsa.projects.savorabudgetapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private val onPhotoClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.apply {
                tvAmount.text = "R${"%.2f".format(expense.amount)}"  // Kept your currency format
                tvCategory.text = expense.Categoryname
                tvDate.text = expense.date
                tvDescription.text = expense.description ?: "No description"

                // Photo handling
                expense.photoUri?.let { uri ->
                    ivPhoto.setImageURI(Uri.parse(uri))
                    ivPhoto.visibility = View.VISIBLE
                    ivPhoto.setOnClickListener { onPhotoClick(expense) }
                } ?: run {
                    ivPhoto.visibility = View.GONE
                }
            }
        }
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
        val expense = getItem(position)
        holder.bind(expense)
    }

    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}