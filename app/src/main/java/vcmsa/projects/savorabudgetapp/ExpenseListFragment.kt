// ExpenseListFragment.kt
package vcmsa.projects.savorabudgetapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import vcmsa.projects.savorabudgetapp.databinding.FragmentExpenseListBinding
import vcmsa.projects.savorabudgetapp.ui.auth.ExpenseListViewModel
import vcmsa.projects.savorabudgetapp.ui.auth.ExpenseListViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseListFragment : Fragment() {
    private lateinit var binding: FragmentExpenseListBinding
    private val viewModel: ExpenseListViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseListViewModelFactory(requireContext())
        ).get(ExpenseListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        val adapter = ExpenseAdapter { expense ->
            // Handle click with the full expense object
        }
        binding.rvExpenses.adapter = adapter
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(emptyList())
    }

    private fun loadData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModel.loadExpenses(today, today)

        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            (binding.rvExpenses.adapter as ExpenseAdapter).submitList(expenses)
        }

        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            // Update your summary UI here
        }
    }
}