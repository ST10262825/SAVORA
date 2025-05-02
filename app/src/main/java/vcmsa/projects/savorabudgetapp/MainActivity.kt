package vcmsa.projects.savorabudgetapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        navController = navHostFragment.navController

        // Setup BottomNavigationView with NavController
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)
    }
}
