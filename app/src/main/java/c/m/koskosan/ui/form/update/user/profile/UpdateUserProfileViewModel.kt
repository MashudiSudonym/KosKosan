package c.m.koskosan.ui.form.update.user.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.UserResponse
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class UpdateUserProfileViewModel(
    private val authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    // Get user uid
    private val userUID: LiveData<String> = authRepository.getUserUid()

    // get user profile data to default field form value
    fun getUserProfileData(): LiveData<ResponseState<UserResponse>> =
        firebaseRepository.readUserProfileData(userUID.value.toString())

    // put user profile data
    fun putUserProfileData(
        name: String,
        imageProfilePath: Uri?,
        phoneNumber: String,
        address: String,
        email: String,
    ): LiveData<ResponseState<Double>> {
        return if (imageProfilePath == null) {
            firebaseRepository.updateUserProfileData(userUID.value.toString(), name, address, email)
        } else {
            firebaseRepository.createUserProfileData(
                userUID.value.toString(),
                name,
                imageProfilePath,
                phoneNumber,
                address,
                email
            )
        }
    }
}