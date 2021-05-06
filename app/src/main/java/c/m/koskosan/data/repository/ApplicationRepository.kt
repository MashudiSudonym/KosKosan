package c.m.koskosan.data.repository

import androidx.lifecycle.LiveData
import c.m.koskosan.data.NetworkBoundResource
import c.m.koskosan.data.entity.LocationEntity
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.util.ContextProviders
import c.m.koskosan.vo.ResponseState

class ApplicationRepository(
    private val localRepository: LocalRepository,
    private val contextProviders: ContextProviders
) {
    fun searchContent(searchKeyword: String): LiveData<ResponseState<List<LocationEntity>>> =
        object :
            NetworkBoundResource<List<LocationEntity>, List<LocationResponse>>(contextProviders) {

            override fun createCall(): LiveData<ResponseState<List<LocationResponse>>>? = null

            override fun shouldFetch(data: List<LocationEntity>?): Boolean =
                data == null || data.isEmpty()

            override fun loadFromDb(): LiveData<List<LocationEntity>> =
                localRepository.searchContent(searchKeyword)

            override suspend fun saveCallResult(item: List<LocationResponse>) {}
        }.asLiveData()
}