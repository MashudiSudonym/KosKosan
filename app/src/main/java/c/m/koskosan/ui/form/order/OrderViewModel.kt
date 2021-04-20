package c.m.koskosan.ui.form.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.data.model.UserResponse
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class OrderViewModel(
    private val authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    // Get User active uid
    private val userUID: LiveData<String> = authRepository.getUserUid()

    // Get user profile data by user uid
    fun getUserProfile(): LiveData<ResponseState<UserResponse>> =
        firebaseRepository.readUserProfileData(userUID.value.toString())

    // Post user order data
    fun postOrder(
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
        rentStop: String
    ): LiveData<ResponseState<OrderResponse>> = firebaseRepository.createOrderData(
        userUID.value.toString(),
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
        rentStop
    )

    // location UID
    private val mutableLocationUID = MutableLiveData<String>()
    val locationUID: LiveData<String> get() = mutableLocationUID

    fun setLocationUID(locationUID: String) {
        mutableLocationUID.value = locationUID
    }

    // location name
    private val mutableLocationName = MutableLiveData<String>()
    val locationName: LiveData<String> get() = mutableLocationName

    fun setLocationName(locationName: String) {
        mutableLocationName.value = locationName
    }

    // location address
    private val mutableLocationAddress = MutableLiveData<String>()
    val locationAddress: LiveData<String> get() = mutableLocationAddress

    fun setLocationAddress(locationAddress: String) {
        mutableLocationAddress.value = locationAddress
    }

    // location phone
    private val mutableLocationPhone = MutableLiveData<String>()
    val locationPhone: LiveData<String> get() = mutableLocationPhone

    fun setLocationPhone(locationPhone: String) {
        mutableLocationPhone.value = locationPhone
    }

    // survey schedule date
    private val mutableSurveyScheduleDate = MutableLiveData<String>()
    val surveyScheduleDate: LiveData<String> get() = mutableSurveyScheduleDate

    fun selectedSurveyScheduleDate(date: String) {
        mutableSurveyScheduleDate.value = date
    }

    // start rent date
    private val mutableStartRentDate = MutableLiveData<String>()
    val startRentDate: LiveData<String> get() = mutableStartRentDate

    fun selectedStartRentDate(date: String) {
        mutableStartRentDate.value = date
    }

    // stop rent date
    private val mutableStopRentDate = MutableLiveData<String>()
    val stopRentDate: LiveData<String> get() = mutableStopRentDate

    fun selectedStopRentDate(date: String) {
        mutableStopRentDate.value = date
    }
}