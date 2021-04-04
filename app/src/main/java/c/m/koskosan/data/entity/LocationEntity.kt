package c.m.koskosan.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val idLocationTable: Long = 0L,
    @ColumnInfo(name = "uid")
    val uid: String = "",
    @ColumnInfo(name = "photo_url")
    val photoURL: String = "",
    @ColumnInfo(name = "name_location")
    val nameLocation: String = ""
): Parcelable