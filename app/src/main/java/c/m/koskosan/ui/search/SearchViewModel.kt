package c.m.koskosan.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.entity.LocationEntity
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.data.repository.ApplicationRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.data.repository.LocalRepository
import c.m.koskosan.vo.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchViewModel(
    private val applicationRepository: ApplicationRepository,
    private val firebaseRepository: FirebaseRepository,
    private val localRepository: LocalRepository
) : ViewModel() {
    // get firestore data and save to local db
    fun getAllLocation(): LiveData<ResponseState<List<LocationResponse>>> =
        firebaseRepository.readLocationData()

    fun saveAllLocation(locationEntity: ArrayList<LocationEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            localRepository.updateContent(locationEntity)
        }
    }

    // with underscore sign, this variable must be private for this class
    private val _searchKeyword: MutableLiveData<String> = MutableLiveData()
    val searchContent: LiveData<ResponseState<List<LocationEntity>>> =
        Transformations.switchMap(_searchKeyword) { keyword ->
            applicationRepository.searchContent(keyword)
        }

    fun getSearchKeyword(searchKeyword: String) {
        _searchKeyword.value = searchKeyword
    }
}