package c.m.koskosan.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class OrderResponse(
    var uid: String? = null,
    var userUID: String? = null,
    var userName: String? = null,
    var userAddress: String? = null,
    var userPhone: String? = null,
    var nameLocation: String? = null,
    var uidLocation: String? = null,
    var addressLocation: String? = null,
    var phoneLocation: String? = null,
    var orderCreated: String? = null,
    var orderStatus: Int? = null,
    var surveySchedule: String? = null,
    var rentStart: String? = null,
    var rentStop: String? = null,
    var locationOwnerUID: String? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "userUID" to userUID,
        "userName" to userName,
        "userAddress" to userAddress,
        "userPhone" to userPhone,
        "nameLocation" to nameLocation,
        "uidLocation" to uidLocation,
        "addressLocation" to addressLocation,
        "phoneLocation" to phoneLocation,
        "orderCreated" to orderCreated,
        "orderStatus" to orderStatus,
        "surveySchedule" to surveySchedule,
        "rentStart" to rentStart,
        "rentStop" to rentStop,
        "locationOwnerUID" to locationOwnerUID,
    )

}
