package c.m.koskosan.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class MapsViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {
    // get all location data
    fun getLocations(): LiveData<ResponseState<List<LocationResponse>>> =
        firebaseRepository.readAllLocations()
}