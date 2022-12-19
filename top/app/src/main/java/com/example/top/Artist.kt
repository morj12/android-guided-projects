package com.example.top

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "surname") var surname: String = "",
    @ColumnInfo(name = "birth_date") var birthDate: Long = 0,
    @ColumnInfo(name = "birth_place") var birthPlace: String= "",
    @ColumnInfo(name = "height") var height: Short = 0,
    @ColumnInfo(name = "order") var order: Int = 0,
    @ColumnInfo(name = "notes") var notes: String = "",
    @ColumnInfo(name = "photo_url") var photoUrl: String = ""
)
{
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val BIRTH_DATE = "birthDate"
        const val BIRTH_PLACE = "birthPlace"
        const val HEIGHT = "height"
        const val ORDER = "order"
        const val NOTES = "notes"
        const val PHOTO_URL = "photoUrl"
    }
}
