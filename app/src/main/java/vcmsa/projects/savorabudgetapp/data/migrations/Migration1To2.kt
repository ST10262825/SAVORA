package vcmsa.projects.savorabudgetapp.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Step 1: Create temporary table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS expenses_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                amount REAL NOT NULL,
                Categoryname TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT NOT NULL
            )
        """)

        // Step 2: Migrate data (convert IDs to names)
        database.execSQL("""
            INSERT INTO expenses_temp (id, amount, Categoryname, date, description)
            SELECT e.id, e.amount, 
                   COALESCE((SELECT c.Categoryname FROM categories c WHERE c.id = e.Categoryname), 'Unknown'),
                   e.date, e.description
            FROM expenses e
        """)

        // Step 3: Drop old table
        database.execSQL("DROP TABLE expenses")

        // Step 4: Rename temp table
        database.execSQL("ALTER TABLE expenses_temp RENAME TO expenses")

        // Step 5: Recreate index
        database.execSQL("CREATE INDEX IF NOT EXISTS index_expenses_Categoryname ON expenses (Categoryname)")
    }
}