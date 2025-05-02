package vcmsa.projects.savorabudgetapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["Categoryname"])] // Keep index for faster queries
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val Categoryname: String,  // Now stores the actual category name (e.g. "Food")
    val date: String,
    val description: String
)