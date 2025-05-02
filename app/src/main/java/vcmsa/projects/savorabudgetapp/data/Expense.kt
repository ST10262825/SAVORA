package vcmsa.projects.savorabudgetapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["Categoryname"],
            childColumns = ["Categoryname"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["Categoryname"])]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val Categoryname: String,
    val date: String,
    val description: String?,
    val photoUri: String? = null
)