package c.m.koskosan.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class DetailViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    // get location detail by uid
    fun getLocationByUid(uid: String): LiveData<ResponseState<LocationResponse>> =
        firebaseRepository.readLocationByUid(uid)
}