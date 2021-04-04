package c.m.koskosan.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import c.m.koskosan.databinding.ActivitySplashscreenBinding
import c.m.koskosan.ui.login.LoginActivity
import c.m.koskosan.ui.main.MainActivity
import c.m.koskosan.util.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashscreenActivity : AppCompatActivity() {
    private val splashscreenViewModel: SplashscreenViewModel by viewModel()
    private lateinit var splashscreenBinding: ActivitySplashscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewBinding initialize
        splashscreenBinding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = splashscreenBinding.root
        setContentView(view)

        // Observe return value of the data
        splashscreenViewModel.isUserAuthenticated().observe(
            this,
            { userAuth ->
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                val loginActivityIntent = Intent(this, LoginActivity::class.java)

                // Using Kotlin Coroutine for create the delay
                GlobalScope.launch {
                    delay(2000L)
                    // if user authentication it's false open Login Activity
                    // else open Main Activity
                    if (!userAuth) {
                        finish()
                        startActivity(loginActivityIntent)
                    } else {
                        finish()
                        startActivity(mainActivityIntent)
                    }
                }
            }
        )
    }

    // check ACCESS FINE LOCATION and ACCESS COARSE LOCATION permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // request permission for get location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSION_REQUEST_LOCATION
        )
    }

    // permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}