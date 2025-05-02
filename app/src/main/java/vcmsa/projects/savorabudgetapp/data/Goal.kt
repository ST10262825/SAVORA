package vcmsa.projects.savorabudgetapp.data

// Goal.kt


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goal(
    @PrimaryKey val id: Int = 0,  // Primary key required
    val min: Double,
    val max: Double
)


