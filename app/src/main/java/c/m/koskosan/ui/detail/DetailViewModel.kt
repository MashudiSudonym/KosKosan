package c.m.koskosan.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class DetailViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    // get location detail by uid
    private lateinit var _locationUidInput: String

    fun setLocationUid(locationUid: String) {
        this._locationUidInput = locationUid
    }

    fun getLocationDetailByUid(): LiveData<ResponseState<LocationResponse>> =
        firebaseRepository.readLocationDetailByLocationUid(_locationUidInput)
}