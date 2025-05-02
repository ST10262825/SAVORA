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

class ExpenseRepository(context: Context) {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val expenseDao = database.expenseDao()
    private val categoryDao = database.categoryDao()

    // Add expense to database
    suspend fun insertExpense(expense: Expense) {
        database.withTransaction {
            // First ensure the category exists
            val Categoryname = getOrCreateCategoryId(expense.Categoryname)
            val expenseWithValidCategory = expense.copy(Categoryname = Categoryname.toString())
            expenseDao.insert(expenseWithValidCategory)
        }
    }

    // Add these methods:
    // In your repository
    suspend fun getAllExpenses(): List<Expense> {
        val expenses = expenseDao.getAllExpenses()
        expenses.forEach {
            Log.d("ExpenseDebug", "Expense: ${it.id}, Category: ${it.Categoryname}")
        }
        return expenses
    }

    suspend fun getSummary(startDate: String, endDate: String): ExpenseDao.ExpenseSummary {
        return expenseDao.getSummary(startDate, endDate)
    }

    // Add category to database
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insert(category)
    }



    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }


    // Helper function to get or create category
    suspend fun getOrCreateCategoryId(categoName: String): Int {
        return categoryDao.getIdByName(categoName) ?: run {
            val newId = categoryDao.insert(Category(Categoryname = categoName))
            if (newId == -1L) {
                categoryDao.getIdByName(categoName)
                    ?: throw Exception("Failed to create or find category")
            } else {
                newId.toInt()
            }
        }
    }
}