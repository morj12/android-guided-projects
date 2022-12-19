package com.example.top.database.actor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Actor(
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
        const val ORDER = "order"
    }
}
