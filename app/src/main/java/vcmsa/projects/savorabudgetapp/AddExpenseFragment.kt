package vcmsa.projects.savorabudgetapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vcmsa.projects.savorabudgetapp.data.Category
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository
import vcmsa.projects.savorabudgetapp.databinding.FragmentAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.categories_array)
        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupDatePicker() {
        binding.etDate.apply {
            setOnClickListener { showDatePicker() }
            keyListener = null
            isFocusable = false
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveExpense()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = formatDate(year, month, day)
                binding.etDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun validateInput(): Boolean {
        return when {
            binding.etAmount.text.isNullOrBlank() -> {
                binding.etAmount.error = "Amount is required"
                false
            }
            binding.etDate.text.isNullOrBlank() -> {
                binding.etDate.error = "Date is required"
                false
            }
            else -> true
        }
    }

    private fun saveExpense() {
        val categoryName = binding.spCategory.selectedItem.toString()
        val amount = binding.etAmount.text.toString().toDouble()
        val date = binding.etDate.text.toString()
        val description = binding.etDescription.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get or create category ID
                val categoryId = expenseRepository.getOrCreateCategoryId(categoryName)
                    ?: expenseRepository.insertCategory(Category(Categoryname = categoryName)).toInt()

                val expense = Expense(
                    amount = amount,
                    Categoryname = categoryName,
                    date = date,
                    description = description
                )

                expenseRepository.insertExpense(expense)
                showToast("Expense added successfully")
                findNavController().popBackStack()
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
