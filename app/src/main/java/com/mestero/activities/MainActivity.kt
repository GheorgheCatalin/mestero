package com.mestero.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mestero.R
import com.mestero.databinding.ActivityMainBinding
import com.mestero.data.UserType
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.utils.Analytics
import com.mestero.utils.LanguageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var accountService: AccountService
    @Inject
    lateinit var firestoreRepository: FirestoreRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // TODO add screenOrientation in manifest - lock screen rotation

        if (accountService.currentUserId.isEmpty()) {
            finish()
            return
        }

        getUserTypeAndSetupNavigation()
    }

    private fun getUserTypeAndSetupNavigation() {
        accountService.fetchUserTypeAsString(
            onResult = { userType -> setupNavigation(userType) }
        )
    }

    private fun setupNavigation(userType: String?) {
        val bottomNavView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up navigation based on user type
        when (userType) {
            UserType.PROVIDER.name -> {
                bottomNavView.menu.clear()
                bottomNavView.inflateMenu(R.menu.provider_bottom_nav_menu)
                navController.setGraph(R.navigation.provider_navigation)
            }
            else -> {
                bottomNavView.menu.clear()
                bottomNavView.inflateMenu(R.menu.bottom_nav_menu)
                navController.setGraph(R.navigation.mobile_navigation)
            }
        }

        // Set up AppBarConfiguration based on user type
        val appBarConfiguration = when (userType) {
            UserType.PROVIDER.name -> AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_my_services,
                    R.id.navigation_add_listing,
                    R.id.navigation_bookings,
                    R.id.navigation_profile
                )
            )
            else -> AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_services,
                    R.id.navigation_bookings,
                    R.id.navigation_profile
                )
            )
        }
        // Set up ActionBar & bottom navigation with NavController & AppBarConfiguration for user type
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

        // Mark cold_start as completed and log time
        Analytics.logAppReady()

        // Ensure re-selecting a bottom nav item returns to its root after navigating deeper in the graph
        // Prevents bottom navigation item stuck in child screens of selected tab
        bottomNavView.setOnItemSelectedListener { item ->
            val destinationId = item.itemId
            // If not already on the destination (selected tab), pop back or navigate  to it
            if (navController.currentDestination?.id != destinationId) {
                // Try to pop back stack to the destination (if it exists in the stack)
                val wasStackPopped = navController.popBackStack(destinationId, false)

                // If not -> navigate to it
                if (!wasStackPopped) {
                    navController.navigate(destinationId)
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LanguageManager.applyLanguage(it) })
    }
}