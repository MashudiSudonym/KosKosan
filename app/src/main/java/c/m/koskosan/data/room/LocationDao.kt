package c.m.koskosan.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import c.m.koskosan.data.entity.LocationEntity
import javax.sql.DataSource

@Dao
interface LocationDao {
    // insert data from remote repository
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: List<LocationEntity>)

    // Delete data from local db, this is for refresh and replace old data
    @Query("DELETE FROM location_table")
    suspend fun deleteLocation()

    // transaction is for refresh the data of local db
    @Transaction
    suspend fun updateLocation(locationEntity: List<LocationEntity>) {
        deleteLocation()
        insertLocation(locationEntity)
    }

    // searching data from local db
    @Query("SELECT * FROM location_table WHERE name_location LIKE '%' || :searchKeyword || '%'")
    fun searchLocation(searchKeyword: String): LiveData<List<LocationEntity>>
}