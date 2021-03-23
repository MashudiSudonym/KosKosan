package c.m.koskosan.data.model

import com.google.firebase.firestore.GeoPoint

data class LocationDistanceResponse(
    var uid: String? = null,
    var name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var coordinate: GeoPoint? = null,
    var photo: List<String>? = null,
    var type: String? = null,
    var distance: Double? = 0.0,
)
