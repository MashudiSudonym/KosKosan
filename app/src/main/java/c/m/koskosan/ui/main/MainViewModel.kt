package c.m.koskosan.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository

class MainViewModel(
    authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    // Get user uid
    private val userUid: LiveData<String> = authRepository.getUserUid()

    // user profile data status not null ?
    fun isUserProfileDataIsNull(): LiveData<Boolean> =
        firebaseRepository.checkUserProfileData(userUid.value.toString())

}