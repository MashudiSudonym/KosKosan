package c.m.koskosan.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.koskosan.databinding.ActivitySplashscreenBinding
import c.m.koskosan.ui.login.LoginActivity
import c.m.koskosan.ui.main.MainActivity
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
}