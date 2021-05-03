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
    authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    // Get User active uid
    private val userUID: LiveData<String> = authRepository.getUserUid()

    // Get user profile data by user uid
    fun getUserProfile(): LiveData<ResponseState<UserResponse>> =
        firebaseRepository.readUserProfileData(userUID.value.toString())

    // location UID
    private val _mutableLocationUID = MutableLiveData<String>()
    val locationUID: LiveData<String> get() = _mutableLocationUID

    fun setLocationUID(locationUID: String) {
        _mutableLocationUID.value = locationUID
    }

    // location name
    private val _mutableLocationName = MutableLiveData<String>()
    val locationName: LiveData<String> get() = _mutableLocationName

    fun setLocationName(locationName: String) {
        _mutableLocationName.value = locationName
    }

    // location address
    private val _mutableLocationAddress = MutableLiveData<String>()
    val locationAddress: LiveData<String> get() = _mutableLocationAddress

    fun setLocationAddress(locationAddress: String) {
        _mutableLocationAddress.value = locationAddress
    }

    // location phone
    private val _mutableLocationPhone = MutableLiveData<String>()
    val locationPhone: LiveData<String> get() = _mutableLocationPhone

    fun setLocationPhone(locationPhone: String) {
        _mutableLocationPhone.value = locationPhone
    }

    // survey schedule date
    private val _mutableSurveyScheduleDate = MutableLiveData<String>()
    val surveyScheduleDate: LiveData<String> get() = _mutableSurveyScheduleDate

    fun selectedSurveyScheduleDate(date: String) {
        _mutableSurveyScheduleDate.value = date
    }

    // start rent date
    private val _mutableStartRentDate = MutableLiveData<String>()
    val startRentDate: LiveData<String> get() = _mutableStartRentDate

    fun selectedStartRentDate(date: String) {
        _mutableStartRentDate.value = date
    }

    // stop rent date
    private val _mutableStopRentDate = MutableLiveData<String>()
    val stopRentDate: LiveData<String> get() = _mutableStopRentDate

    fun selectedStopRentDate(date: String) {
        _mutableStopRentDate.value = date
    }

    // Post user order data
    private lateinit var _userNameInput: String
    private lateinit var _userAddressInput: String
    private lateinit var _userPhoneInput: String
    private lateinit var _orderCreatedInput: String
    private var _orderStatusInput: Int = 0

    fun setUserOrderData(
        userName: String,
        userAddress: String,
        userPhone: String,
        orderCreated: String,
        orderStatus: Int,
    ) {
        this._userNameInput = userName
        this._userAddressInput = userAddress
        this._userPhoneInput = userPhone
        this._orderCreatedInput = orderCreated
        this._orderStatusInput = orderStatus
    }

    fun postOrder(): LiveData<ResponseState<OrderResponse>> = firebaseRepository.createOrderData(
        userUID.value.toString(),
        _userNameInput,
        _userAddressInput,
        _userPhoneInput,
        _mutableLocationName.value.toString(),
        _mutableLocationUID.value.toString(),
        _mutableLocationAddress.value.toString(),
        _mutableLocationPhone.value.toString(),
        _orderCreatedInput,
        _orderStatusInput,
        _mutableSurveyScheduleDate.value.toString(),
        _mutableStartRentDate.value.toString(),
        _mutableStopRentDate.value.toString()
    )
}