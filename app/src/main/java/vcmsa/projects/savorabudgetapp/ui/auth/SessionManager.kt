package vcmsa.projects.savorabudgetapp.ui.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("SavoraSession", Context.MODE_PRIVATE)
    }

    fun saveUserSession(userId: String, username: String, email: String) {
        prefs.edit {
            putString("userId", userId)
            putString("username", username)
            putString("email", email)
            putBoolean("isLoggedIn", true)
        }
    }

    fun getCurrentUserId(): String? = prefs.getString("userId", null)
    fun getCurrentUsername(): String? = prefs.getString("username", null)
    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun clearSession() {
        prefs.edit { clear() }
    }
}