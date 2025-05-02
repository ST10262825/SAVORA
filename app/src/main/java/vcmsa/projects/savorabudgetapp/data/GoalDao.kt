package vcmsa.projects.savorabudgetapp.data

import androidx.room.*
import vcmsa.projects.savorabudgetapp.data.Goal

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM Goal WHERE id = 1 LIMIT 1")
    suspend fun getGoal(): Goal?
}
