package c.m.koskosan.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationResponse(
    var uid: String? = null,
    var name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var coordinate: GeoPoint? = null,
    var photo: List<String>? = null,
    var type: String? = null,
    var googlePlace: String? = null,
) {
    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "address" to address,
        "phone" to phone,
        "coordinate" to coordinate,
        "photo" to photo,
        "type" to type,
        "googlePlace" to googlePlace,
    )
}