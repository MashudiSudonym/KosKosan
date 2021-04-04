package c.m.koskosan.data.repository

import androidx.lifecycle.LiveData
import c.m.koskosan.data.entity.LocationEntity
import c.m.koskosan.data.room.LocationDao

class LocalRepository(private val locationDao: LocationDao) {
    // refresh local db
    suspend fun updateContent(locationEntity: List<LocationEntity>) =
        locationDao.updateLocation(locationEntity)

    // searching data
    fun searchContent(searchKeyword: String): LiveData<List<LocationEntity>> =
        locationDao.searchLocation(searchKeyword)
}