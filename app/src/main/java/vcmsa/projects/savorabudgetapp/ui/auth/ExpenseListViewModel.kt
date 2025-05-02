package vcmsa.projects.savorabudgetapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository
import vcmsa.projects.savorabudgetapp.data.CategorySpending

class ExpenseListViewModel(private val repository: ExpenseRepository) : ViewModel() {
    // LiveData for the UI to observe
    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    private val _categorySummary = MutableLiveData<List<CategorySpending>>()
    val categorySummary: LiveData<List<CategorySpending>> = _categorySummary

    /**
     * Loads expenses between the given dates and updates LiveData objects
     * Uses Dispatchers.IO for background thread work
     */
    fun loadExpenses(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) { // Switch to IO thread for DB work
            val expensesList = repository.getExpensesBetweenDates(startDate, endDate) // Fetch from repo
            withContext(Dispatchers.Main) {
                _expenses.value = expensesList // Update LiveData on the main thread
            }
        }
    }

    /**
     * Gets spending by category between given dates
     * Uses Dispatchers.IO for background thread work
     */
    fun loadCategorySummary(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) { // Switch to IO thread for DB work
            val categorySpendingList = repository.getSpendingByCategory(startDate, endDate) // Fetch from repo
            withContext(Dispatchers.Main) {
                _categorySummary.value = categorySpendingList // Update LiveData on the main thread
            }
        }
    }
}

/**
 * Data class representing spending by category
 */
data class CategorySpending(
    val categoryName: String,
    val totalAmount: Double
)
