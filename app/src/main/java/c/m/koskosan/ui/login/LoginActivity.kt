package c.m.koskosan.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityLoginBinding
import c.m.koskosan.ui.main.MainActivity
import c.m.koskosan.util.ViewUtilities.snackBarWarningLong
import c.m.koskosan.util.requestPermission
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import timber.log.Timber

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var layout: View
    private var authUILauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val response = IdpResponse.fromResultIntent(result.data)

            if (result.resultCode == Activity.RESULT_OK) {
                // stop login activity
                finish()
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                // if login result accepted, user will be parsing to main activity
                startActivity(mainActivityIntent)
            } else {
                if (response == null) {
                    // if user canceled the login process, stay on this activity and show alert
                    layout.snackBarWarningLong(
                        getString(R.string.alert_login_cancel)
                    )
                }

                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    // if login process error because user connection internet interruption, show alert about it
                    layout.snackBarWarningLong(
                        getString(R.string.alert_check_internet_connection)
                    )
                }

                Timber.e(
                    "Sign In error: ${response?.error?.message} || ${response?.error}"
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding initialize
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        layout = view
        setContentView(view)

        // request location permission
        requestPermission()

        // login button
        loginBinding.btnLogin.setOnClickListener {
            // with FirebaseUI library, help me to build user login with phone number
            authUILauncher.launch(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setDefaultCountryIso(getString(R.string.default_code_country))
                                .build()
                        )
                    ).build()
            )
        }
    }
}