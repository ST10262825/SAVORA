package vcmsa.projects.savorabudgetapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.ExpenseDao
import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository

class ExpenseListViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    private val _summary = MutableLiveData<ExpenseDao.ExpenseSummary>()
    val summary: LiveData<ExpenseDao.ExpenseSummary> = _summary


//    fun loadExpenses(startDate: String, endDate: String) {
//        viewModelScope.launch {
//            _expenses.value = repository.getExpensesBetweenDates(startDate, endDate)
//            // or use getAllExpenses() if you don't need date filtering
//        }
  fun loadExpenses(startDate: String, endDate: String) {
      viewModelScope.launch {
            _expenses.value = repository.getAllExpenses()
            _summary.value = repository.getSummary(startDate, endDate)
      }
    }
    }
