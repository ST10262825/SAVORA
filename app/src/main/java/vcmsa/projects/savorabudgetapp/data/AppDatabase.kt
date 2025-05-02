package vcmsa.projects.savorabudgetapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        User::class,
        Category::class,
        Expense::class
    ],
    version = 14,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 10 to 11 (categoryId -> Categoryname)
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create new table with Categoryname column
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `expenses_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `amount` REAL NOT NULL,
                        `Categoryname` TEXT NOT NULL,  // Capital C to match entity
                        `date` TEXT NOT NULL,
                        `description` TEXT,
                        FOREIGN KEY(`Categoryname`) REFERENCES `categories`(`Categoryname`) 
                            ON UPDATE CASCADE ON DELETE CASCADE
                    )
                """)

                // 2. Migrate data (convert IDs to names)
                database.execSQL("""
                    INSERT INTO `expenses_new` (id, amount, Categoryname, date, description)
                    SELECT e.id, e.amount, 
                           COALESCE(c.Categoryname, 'Unknown') as Categoryname,
                           e.date, e.description
                    FROM expenses AS e
                    LEFT JOIN categories c ON e.categoryId = c.id
                """)

                // 3. Replace old table
                database.execSQL("DROP TABLE expenses")
                database.execSQL("ALTER TABLE expenses_new RENAME TO expenses")

                // 4. Create index
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_expenses_Categoryname` 
                    ON `expenses` (`Categoryname`)
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "savora_database"
                )
                    .addMigrations(MIGRATION_10_11)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}