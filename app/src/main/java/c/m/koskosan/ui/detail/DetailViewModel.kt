package c.m.koskosan.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class DetailViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    // get location detail by uid
    lateinit var uidInput: String

    fun setUIDInput(uid: String) {
        this.uidInput = uid
    }

    fun getLocationByUid(): LiveData<ResponseState<LocationResponse>> =
        firebaseRepository.readLocationByUid(uidInput)
}