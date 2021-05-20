package c.m.koskosan.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.data.model.UserResponse
import c.m.koskosan.vo.ResponseState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import kotlin.math.ceil

class FirebaseRepository {
    // Firebase Storage instance
    private val storage: FirebaseStorage = Firebase.storage
    private val userProfileStorageReference: StorageReference = storage.reference.child("users")

    // Firestore instance
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val userProfileCollection: CollectionReference = firestore.collection("users")
    private val locationCollection: CollectionReference = firestore.collection("locations")
    private val orderCollection: CollectionReference = firestore.collection("orders")

    // Check user data from users collection firestore
    fun checkUserProfileData(userUID: String): LiveData<Boolean> {
        val isUserProfileData: MutableLiveData<Boolean> = MutableLiveData()
        userProfileCollection.whereEqualTo("uid", userUID).limit(1).get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot?.toObjects(UserResponse::class.java)

                // this return to false if users have a data
                isUserProfileData.value = users?.isNotEmpty() == false
            }
            .addOnFailureListener { error ->
                Timber.e("$error")
            }

        return isUserProfileData
    }

    // post user profile data to firestore and user profile image to storage
    fun createUserProfileData(
        userUID: String,
        name: String,
        imageProfilePath: Uri?,
        phoneNumber: String,
        address: String,
        email: String,
    ): LiveData<ResponseState<Double>> {
        val progressUploadingData: MutableLiveData<ResponseState<Double>> = MutableLiveData()
        val imageReference: StorageReference = userProfileStorageReference.child("$userUID/profile")
        val progressDone = 100.0

        if (imageProfilePath != null) {
            imageReference.putFile(imageProfilePath)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        val mapUserResponseData = UserResponse(
                            userUID,
                            name,
                            (uri?.toString() ?: "-"),
                            phoneNumber,
                            address,
                            email
                        )

                        userProfileCollection.document(userUID).set(mapUserResponseData)
                            .addOnSuccessListener {
                                // update user order data
                                updateUserOrderData(
                                    userUID,
                                    address,
                                    name,
                                    phoneNumber,
                                    progressUploadingData,
                                    progressDone
                                )
                            }
                            .addOnFailureListener { exception ->
                                progressUploadingData.value =
                                    ResponseState.Error(exception.localizedMessage, null)
                            }
                    }
                }
                .addOnFailureListener {
                    progressUploadingData.value = ResponseState.Error("upload image failed", null)
                }
                .addOnProgressListener { snapshot ->
                    val progressCount: Double =
                        100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
                    progressUploadingData.value = ResponseState.Loading(ceil(progressCount))
                }
        } else {
            progressUploadingData.value = ResponseState.Error("image profile not found", null)
        }

        return progressUploadingData
    }

    // update user order data

    private fun updateUserOrderData(
        userUID: String,
        address: String,
        name: String,
        phoneNumber: String,
        progressUploadingData: MutableLiveData<ResponseState<Double>>,
        progressDone: Double
    ) {
        orderCollection.whereEqualTo("userUID", userUID).get()
            .addOnSuccessListener { snapshot ->
                val orders = snapshot?.toObjects(OrderResponse::class.java)

                if (orders != null) {
                    orders.forEach { data ->
                        orderCollection.document(data.uid.toString())
                            .update(
                                mapOf(
                                    "userAddress" to address,
                                    "userName" to name,
                                    "userPhone" to phoneNumber
                                )
                            ).addOnSuccessListener {
                                // process upload done
                                progressUploadingData.value =
                                    ResponseState.Success(progressDone)
                            }
                            .addOnFailureListener { exception ->
                                Timber.e(exception.localizedMessage)

                                // process upload done
                                progressUploadingData.value =
                                    ResponseState.Success(progressDone)
                            }
                    }
                } else {
                    Timber.e("null")

                    // process upload done
                    progressUploadingData.value =
                        ResponseState.Success(progressDone)
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception.localizedMessage)

                // process upload done
                progressUploadingData.value =
                    ResponseState.Success(progressDone)
            }
    }

    // update user profile data
    fun updateUserProfileData(
        userUid: String,
        name: String,
        address: String,
        phoneNumber: String,
        email: String
    ): LiveData<ResponseState<Double>> {
        val progressUploadingData: MutableLiveData<ResponseState<Double>> = MutableLiveData()
        val progressDone = 100.0
        val mapUserProfileData = mapOf(
            "uid" to userUid,
            "name" to name,
            "address" to address,
            "email" to email
        )

        // Loading State
        progressUploadingData.value = ResponseState.Loading(0.0)

        userProfileCollection.document(userUid)
            .update(mapUserProfileData)
            .addOnSuccessListener {
                // update user order data
                updateUserOrderData(
                    userUid,
                    address,
                    name,
                    phoneNumber,
                    progressUploadingData,
                    progressDone
                )
                progressUploadingData.value = ResponseState.Success(progressDone)
            }
            .addOnFailureListener { exception ->
                progressUploadingData.value = ResponseState.Error(exception.localizedMessage, null)
            }

        return progressUploadingData
    }

    // get user profile data by user uid
    fun readUserProfileData(userUid: String): LiveData<ResponseState<UserResponse>> {
        val userProfileData: MutableLiveData<ResponseState<UserResponse>> = MutableLiveData()

        // Loading State
        userProfileData.value = ResponseState.Loading(null)

        userProfileCollection.whereEqualTo("uid", userUid).limit(1).get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot?.toObjects(UserResponse::class.java)

                if (users != null) {
                    users.forEach { data ->

                        userProfileData.value = ResponseState.Success(data)
                    }
                } else {
                    userProfileData.value = ResponseState.Error("No Data")
                }

            }
            .addOnFailureListener { error ->
                userProfileData.value = ResponseState.Error(error.localizedMessage)
            }

        return userProfileData
    }

    // get all locations
    fun readAllLocations(): LiveData<ResponseState<List<LocationResponse>>> {
        val locations: MutableLiveData<ResponseState<List<LocationResponse>>> = MutableLiveData()

        // loading state
        locations.value = ResponseState.Loading(null)

        locationCollection.get().addOnSuccessListener { snapshot ->
            val locationSnapshot = snapshot?.toObjects(LocationResponse::class.java)

            // success state
            locations.value = ResponseState.Success(locationSnapshot)
        }.addOnFailureListener { exception ->
            // error state
            locations.value = ResponseState.Error(exception.localizedMessage, null)
        }

        return locations
    }

    // get location by location uid
    fun readLocationDetailByLocationUid(locationUid: String): LiveData<ResponseState<LocationResponse>> {
        val location: MutableLiveData<ResponseState<LocationResponse>> = MutableLiveData()

        // Loading state
        location.value = ResponseState.Loading(null)

        locationCollection.whereEqualTo("uid", locationUid).get()
            .addOnSuccessListener { snapshot ->
                val locationSnapshot = snapshot?.toObjects(LocationResponse::class.java)

                if (locationSnapshot != null) {
                    locationSnapshot.forEach { data ->
                        // success state
                        location.value = ResponseState.Success(data)
                    }
                } else {
                    // error state
                    location.value = ResponseState.Error("No data", null)
                }
            }
            .addOnFailureListener { exception ->
                // error state
                location.value = ResponseState.Error(exception.localizedMessage, null)
            }

        return location
    }

    // create order
    fun createOrderData(
        userUID: String,
        userName: String,
        userAddress: String,
        userPhone: String,
        nameLocation: String,
        uidLocation: String,
        addressLocation: String,
        phoneLocation: String,
        orderCreated: String,
        orderStatus: Int,
        surveySchedule: String,
        rentStart: String,
        rentStop: String,
        locationOwnerUID: String,
    ): LiveData<ResponseState<OrderResponse>> {
        val order: MutableLiveData<ResponseState<OrderResponse>> = MutableLiveData()
        val randomOrderUID = orderCollection.document().id
        val mapOrderData = OrderResponse(
            randomOrderUID,
            userUID,
            userName,
            userAddress,
            userPhone,
            nameLocation,
            uidLocation,
            addressLocation,
            phoneLocation,
            orderCreated,
            orderStatus,
            surveySchedule,
            rentStart,
            rentStop,
            locationOwnerUID
        ).toMap()

        // show loading state
        order.value = ResponseState.Loading(null)

        orderCollection.document(randomOrderUID).set(mapOrderData).addOnSuccessListener {
            // show success state
            order.value = ResponseState.Success(null)
        }
            .addOnFailureListener { exception ->
                // show error state
                order.value = ResponseState.Error(exception.localizedMessage, null)
            }
        return order
    }

    // get user order by user uid
    fun readOrderByUserUid(userUid: String): LiveData<ResponseState<List<OrderResponse>>> {
        val orders: MutableLiveData<ResponseState<List<OrderResponse>>> = MutableLiveData()

        // show loading state
        orders.value = ResponseState.Loading(null)

        orderCollection.whereEqualTo("userUID", userUid)
            .orderBy("orderCreated", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val orderSnapshot = snapshot?.toObjects(OrderResponse::class.java)

                // success state
                orders.value = ResponseState.Success(orderSnapshot)
            }
            .addOnFailureListener { exception ->
                // error state
                orders.value = ResponseState.Error(exception.localizedMessage, null)
            }

        return orders
    }

    // get user order details by order uid
    fun readOrderDetailByOrderUid(orderUid: String): LiveData<ResponseState<OrderResponse>> {
        val orders: MutableLiveData<ResponseState<OrderResponse>> = MutableLiveData()

        // show loading state
        orders.value = ResponseState.Loading(null)

        orderCollection.whereEqualTo("uid", orderUid).get()
            .addOnSuccessListener { snapshot ->
                val orderSnapshot = snapshot?.toObjects(OrderResponse::class.java)

                // success state
                if (orderSnapshot != null) {
                    orderSnapshot.forEach { data ->
                        // success state
                        orders.value = ResponseState.Success(data)
                    }
                } else {
                    // error state
                    orders.value = ResponseState.Error("No data", null)
                }
            }
            .addOnFailureListener { exception ->
                // error state
                orders.value = ResponseState.Error(exception.localizedMessage, null)
            }

        return orders
    }
}