package vcmsa.projects.savorabudgetapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import vcmsa.projects.savorabudgetapp.ui.auth.SessionManager
import javax.inject.Inject
import android.content.Intent
import vcmsa.projects.savorabudgetapp.ui.auth.LoginActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // TEMPORARY: Always go to LoginActivity for testing
//        sessionManager.clearSession() // Clear any existing session
//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()
//
//        /* REAL IMPLEMENTATION (comment out above when done testing)
//        if (sessionManager.isLoggedIn()) {
//            startActivity(Intent(this, MainActivity::class.java))
//        } else {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//        finish()
//        */
//    }
}