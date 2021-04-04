package c.m.koskosan.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import c.m.koskosan.data.entity.LocationEntity

@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocationDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}