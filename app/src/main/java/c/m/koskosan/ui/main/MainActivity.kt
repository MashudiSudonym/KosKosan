package c.m.koskosan.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityMainBinding
import c.m.koskosan.ui.form.add.user.profile.AddUserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding initialize
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        // initialize layout for using widget utilities
        layout = view

        // Bottom Navigation and Navigation Controller
        val navView: BottomNavigationView = mainBinding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Attach view of bottom navigation to navigation controller
        navView.setupWithNavController(navController)

        // handle menu disable
        navView.background = null
        navView.menu.getItem(2).isEnabled = false

        // handling fab button for open navigation maps fragment
        mainBinding.fabBottomNav.setOnClickListener { navController.navigate(R.id.navigation_maps) }

        // Observe return value of the data
        mainViewModel.isUserProfileDataNotNull().observe(this, { userProfileData ->
            val addUserProfileIntent = Intent(this, AddUserProfileActivity::class.java)
            // if user profile data not null to be true it's open form add user profile screen
            // else do nothing, stay on this activity
            if (userProfileData) {
                finish()
                startActivity(addUserProfileIntent)
            }
        })
    }
}