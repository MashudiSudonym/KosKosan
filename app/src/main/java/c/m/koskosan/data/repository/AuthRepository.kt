package c.m.koskosan.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * AuthRepository Class, this class it's source of authentication data
 */

class AuthRepository {
    // Initialize FirebaseAuth for source of data
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Do checking about user authentication status.
    // if user not null authentication status it's true
    // else authentication status it's false
    fun checkUserAuthenticated(): LiveData<Boolean> {
        val isUserAuthenticated:MutableLiveData<Boolean> = MutableLiveData()
        val firebaseUser: Boolean = firebaseAuth.currentUser != null

        isUserAuthenticated.value = firebaseUser

        return isUserAuthenticated
    }

    // Do get the user phone number
    fun getUserPhoneNumber(): LiveData<String> {
        val userPhoneNumber: MutableLiveData<String> = MutableLiveData()
        val firebaseUser: FirebaseUser = firebaseAuth.currentUser as FirebaseUser

        userPhoneNumber.value = firebaseUser.phoneNumber

        return userPhoneNumber
    }

    // Do get the user unique id (UID)
    fun getUserUid(): LiveData<String> {
        val userUid: MutableLiveData<String> = MutableLiveData()
        val firebaseUser: FirebaseUser = firebaseAuth.currentUser as FirebaseUser

        userUid.value = firebaseUser.uid

        return userUid
    }
}