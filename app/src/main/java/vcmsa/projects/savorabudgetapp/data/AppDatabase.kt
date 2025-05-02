package vcmsa.projects.savorabudgetapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import vcmsa.projects.savorabudgetapp.data.User
import vcmsa.projects.savorabudgetapp.data.Category
import vcmsa.projects.savorabudgetapp.data.Expense
import vcmsa.projects.savorabudgetapp.data.UserDao
import vcmsa.projects.savorabudgetapp.data.CategoryDao
import vcmsa.projects.savorabudgetapp.data.ExpenseDao

@Database(
    entities = [
        User::class,
        Category::class,
        Expense::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "savora_database"
                )
                    .fallbackToDestructiveMigration() // <-- this is what step 3 is about
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
