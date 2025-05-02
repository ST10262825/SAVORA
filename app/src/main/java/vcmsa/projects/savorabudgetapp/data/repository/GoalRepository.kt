package vcmsa.projects.savorabudgetapp.data.repository

import android.content.Context
import vcmsa.projects.savorabudgetapp.data.AppDatabase
import vcmsa.projects.savorabudgetapp.data.Goal

class GoalRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.goalDao()

    suspend fun saveGoals(min: Double, max: Double) {
        dao.insertGoal(Goal(min = min, max = max))
    }

    suspend fun getGoals(): Goal? = dao.getGoal()
}
