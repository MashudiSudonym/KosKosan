package c.m.koskosan.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * User Model class, for handling return of value user data from source of data
 */

@IgnoreExtraProperties
data class UserResponse(
    var uid: String? = null,
    var name: String? = null,
    var imageProfile: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var email: String? = null,
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "imageProfile" to imageProfile,
        "phoneNumber" to phoneNumber,
        "address" to address,
        "email" to email,
    )
}
