package vcmsa.projects.savorabudgetapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import vcmsa.projects.savorabudgetapp.data.CategorySpending
import vcmsa.projects.savorabudgetapp.databinding.FragmentExpenseListBinding

import vcmsa.projects.savorabudgetapp.ui.auth.ExpenseListViewModel
import vcmsa.projects.savorabudgetapp.ui.auth.ExpenseListViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseListFragment : Fragment() {
    private lateinit var binding: FragmentExpenseListBinding
    private lateinit var adapter: ExpenseAdapter

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
        setupDateRangeSelector()
        setupCategorySummary()
        loadDefaultData()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->
            expense.photoUri?.let { uri ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(uri), "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(intent)
            }
        }

        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ExpenseListFragment.adapter
        }
    }

    private fun setupDateRangeSelector() {
        binding.btnSelectDateRange.setOnClickListener {
            showDateRangeDialog()
        }
    }

    private fun showDateRangeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_range, null)
        val etStartDate = dialogView.findViewById<EditText>(R.id.etStartDate)
        val etEndDate = dialogView.findViewById<EditText>(R.id.etEndDate)

        val calendar = Calendar.getInstance()
        val startOfMonth = calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        etStartDate.setText(dateFormat.format(startOfMonth.time))
        etEndDate.setText(dateFormat.format(Calendar.getInstance().time))

        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val startDate = etStartDate.text.toString()
                val endDate = etEndDate.text.toString()
                loadData(startDate, endDate)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                editText.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadDefaultData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        loadData(today, today)
    }

    private fun loadData(startDate: String, endDate: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadExpenses(startDate, endDate)
            viewModel.loadCategorySummary(startDate, endDate)

            viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
                adapter.submitList(expenses)
            }

            viewModel.categorySummary.observe(viewLifecycleOwner) { summary ->
                binding.tvCategorySummary.text = formatCategorySummary(summary)
            }
        }
    }

    private fun formatCategorySummary(summary: List<CategorySpending>): String {
        return summary.joinToString("\n") {
            "${it.Categoryname}: $${it.totalAmount}"
        }
    }

    private fun setupCategorySummary() {
        binding.btnToggleSummary.setOnClickListener {
            val isVisible = binding.tvCategorySummary.visibility == View.VISIBLE
            binding.tvCategorySummary.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }
}
