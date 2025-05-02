package vcmsa.projects.savorabudgetapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vcmsa.projects.savorabudgetapp.data.CategorySpending
// import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository

class DashboardViewModel(
   // private val repository: ExpenseRepository,
   // private val categoryRepository: CategoryRepository // Assume you have this
) : ViewModel() {

    // LiveData for category spending (with category names)
//    val categorySpending: LiveData<List<CategorySpendingUI>> =
//        repository.getSpendingByCategory().switchMap { spendingList ->
//            combineWithCategoryNames(spendingList)
//        }

    private fun combineWithCategoryNames(
        spendingList: List<CategorySpending>
    ): LiveData<List<CategorySpendingUI>> {
        val result = MutableLiveData<List<CategorySpendingUI>>()

        viewModelScope.launch {
            val uiList = spendingList.map { spending ->
             //   val categoryName = categoryRepository.getCategoryName(spending.categoryId)
              //  CategorySpendingUI(categoryName, spending.totalAmount)
            }
          //  result.postValue(uiList)
        }

        return result
    }
}

// UI-friendly data class
data class CategorySpendingUI(
    val categoryName: String,
    val amount: Double
)