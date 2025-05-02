package vcmsa.projects.savorabudgetapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    // Basic operations
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>

    // Date-filtered operations
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun observeExpensesBetweenDates(startDate: String, endDate: String): LiveData<List<Expense>>

    // Summary operations
    @Query("SELECT SUM(amount) as total, COUNT(*) as count FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getSummary(startDate: String, endDate: String): ExpenseSummary

    @Query("SELECT SUM(amount) as total, COUNT(*) as count FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun observeSummary(startDate: String, endDate: String): LiveData<ExpenseSummary>

    // Category spending
    @Query("""
        SELECT Categoryname as Categoryname, SUM(amount) as totalAmount 
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY Categoryname
    """)
    suspend fun getSpendingByCategory(startDate: String, endDate: String): List<CategorySpending>

    @Query("""
        SELECT Categoryname as Categoryname, SUM(amount) as totalAmount 
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY Categoryname
    """)
    fun observeSpendingByCategory(startDate: String, endDate: String): LiveData<List<CategorySpending>>

    // Data class for Expense summary
    data class ExpenseSummary(
        val total: Double,
        val count: Int
    )


}
