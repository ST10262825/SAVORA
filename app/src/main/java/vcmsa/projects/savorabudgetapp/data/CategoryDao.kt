package vcmsa.projects.savorabudgetapp.data


import androidx.room.*
import vcmsa.projects.savorabudgetapp.data.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category): Long

    @Query("SELECT * FROM categories WHERE Categoryname = :name LIMIT 1")
    suspend fun getByName(name: String): Category?

    @Query("SELECT id FROM categories WHERE Categoryname = :name LIMIT 1")
    suspend fun getIdByName(name: String): Int?

    // Add this to your CategoryDao
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): Category?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<Category>
}