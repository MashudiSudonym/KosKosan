package c.m.koskosan.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.repository.AuthRepository

class SplashscreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // user it's login ?
    fun isUserAuthenticated(): LiveData<Boolean> = authRepository.checkUserAuthenticated()
}