package c.m.koskosan.ui.form.add.user.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


class AddUserProfileViewModel(
    private val authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val coroutineContext: CoroutineContext =
        viewModelScope.coroutineContext + Dispatchers.IO

    // Get user uid
    private val userUID: LiveData<String> = authRepository.getUserUid()

    // Get user phone number
    fun getUserPhoneNumber(): LiveData<String> = authRepository.getUserPhoneNumber()

    // Post user profile data and return of uploading data progress number
    fun postUserProfileData(
        name: String,
        imageProfilePath: Uri,
        phoneNumber: String,
        address: String,
        email: String
    ): LiveData<ResponseState<Double>> = firebaseRepository.createUserProfileData(
        userUID.value.toString(),
        name,
        imageProfilePath,
        phoneNumber,
        address,
        email
    )
}