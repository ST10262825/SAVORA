package vcmsa.projects.savorabudgetapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Categoryname: String,
//    val color: String  // e.g., "#FF6384" for chart colors
)