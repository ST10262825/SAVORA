package vcmsa.projects.savorabudgetapp.data

data class CategorySpending(
    val Categoryname: Int,
    val totalAmount: Double
) {
    // Optional: Add a method to get name if needed
    fun getCategoryName(): String {
        return when(Categoryname) {
            1 -> "Groceries"
            2 -> "Transport"
            // ... other categories
            else -> "Other"
        }
    }
}