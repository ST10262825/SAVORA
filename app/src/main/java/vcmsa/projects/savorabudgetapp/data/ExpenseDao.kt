package vcmsa.projects.savorabudgetapp.data


import androidx.lifecycle.LiveData
import androidx.room.*
import vcmsa.projects.savorabudgetapp.data.Expense

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE Categoryname = :categoryName")
    suspend fun getExpensesByCategory(categoryName: String): List<Expense>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>

    @Query("""
        SELECT SUM(amount) as total, COUNT(*) as count 
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    suspend fun getSummary(startDate: String, endDate: String): ExpenseSummary

    @Query("""
        SELECT Categoryname, SUM(amount) as totalAmount 
        FROM expenses 
        GROUP BY Categoryname
    """)
    fun getSpendingByCategory(): LiveData<List<CategorySpending>>

    // Data classes should be outside the DAO interface ideally,
    // but can work here if marked as static (in Java terms)
    data class ExpenseSummary(
        val total: Double,
        val count: Int
    )

    data class CategorySpending(
        val Categoryname: String,
        val totalAmount: Double
    )
}