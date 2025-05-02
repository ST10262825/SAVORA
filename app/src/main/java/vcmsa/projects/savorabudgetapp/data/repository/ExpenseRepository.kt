package vcmsa.projects.savorabudgetapp.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import vcmsa.projects.savorabudgetapp.data.AppDatabase
import vcmsa.projects.savorabudgetapp.data.Category
import vcmsa.projects.savorabudgetapp.data.CategoryDao
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.ExpenseDao
import vcmsa.projects.savorabudgetapp.data.CategorySpending

class ExpenseRepository(context: Context) {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val expenseDao = database.expenseDao()
    private val categoryDao = database.categoryDao()

    // Expense operations
    suspend fun insertExpense(expense: Expense) {
        database.withTransaction {
            ensureCategoryExists(expense.Categoryname)
            expenseDao.insert(expense)
        }
    }

    suspend fun getAllExpenses(): List<Expense> {
        return expenseDao.getAllExpenses().also { expenses ->
            expenses.forEach {
                Log.d("ExpenseDebug", "Expense: ${it.id}, Category: ${it.Categoryname}")
            }
        }
    }

    // Date-filtered operations
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }

    fun observeExpensesBetweenDates(startDate: String, endDate: String): LiveData<List<Expense>> {
        return expenseDao.observeExpensesBetweenDates(startDate, endDate)
    }

    // Summary operations
    suspend fun getSummary(startDate: String, endDate: String): ExpenseDao.ExpenseSummary {
        return expenseDao.getSummary(startDate, endDate)
    }

    fun observeSummary(startDate: String, endDate: String): LiveData<ExpenseDao.ExpenseSummary> {
        return expenseDao.observeSummary(startDate, endDate)
    }

    // Category spending operations
    suspend fun getSpendingByCategory(startDate: String, endDate: String): List<CategorySpending> {
        return expenseDao.getSpendingByCategory(startDate, endDate)
    }

    fun observeSpendingByCategory(startDate: String, endDate: String): LiveData<List<CategorySpending>> {
        return expenseDao.observeSpendingByCategory(startDate, endDate)
    }

    // Category operations
    suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    public suspend fun ensureCategoryExists(name: String) {
        if (categoryDao.getIdByName(name) == null) {
            categoryDao.insert(Category(Categoryname = name))
        }
    }
}
