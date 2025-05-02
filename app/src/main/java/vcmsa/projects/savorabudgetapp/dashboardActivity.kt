package vcmsa.projects.savorabudgetapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import vcmsa.projects.savorabudgetapp.data.AppDatabase
// import vcmsa.projects.savorabudgetapp.data.repository.ExpenseRepository
import vcmsa.projects.savorabudgetapp.ui.auth.CategorySpendingUI
import vcmsa.projects.savorabudgetapp.ui.auth.DashboardViewModel

class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize ViewModel (use ViewModelProvider)
       // val repository = ExpenseRepository(AppDatabase.getDatabase(this).expenseDao())
       // val categoryRepository = CategoryRepository() // Implement this
       // viewModel = ViewModelProvider(this, DashboardViewModelFactory(repository, categoryRepository))
        //    .get(DashboardViewModel::class.java)

        // Observe category spending
//        viewModel.categorySpending.observe(this) { spendingList ->
//            updatePieChart(spendingList)
//        }
    }

    private fun updatePieChart(data: List<CategorySpendingUI>) {
        val entries = data.map {
            PieEntry(it.amount.toFloat(), it.categoryName)
        }
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#FF6384"),  // Red
                Color.parseColor("#36A2EB"),  // Blue
                Color.parseColor("#FFCE56")   // Yellow
            )
        }
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }
}