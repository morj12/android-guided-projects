package com.example.favourite_movies.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "Movie",
    foreignKeys = [ForeignKey(
        entity = Genre::class,
        parentColumns = ["id"],
        childColumns = ["genre_id"],
        onDelete = CASCADE)]
)
data class Movie(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private var _id: Long = 0,

    @ColumnInfo(name = "name")
    private var _name: String,

    @ColumnInfo(name = "description")
    private var _description: String,

    @ColumnInfo(name = "genre_id")
    private var _genreId: Long

) : BaseObservable() {
    var id: Long
        @Bindable get() = _id
        set(value) {
            _id = value
            notifyPropertyChanged(BR.id)
        }

    var name: String
        @Bindable get() = _name
        set(value) {
            _name = value
            notifyPropertyChanged(BR.name)
        }

    var description: String
        @Bindable get() = _description
        set(value) {
            _description = value
            notifyPropertyChanged(BR.description)
        }

    var genreId: Long
        @Bindable get() = _genreId
        set(value) {
            _genreId = value
            notifyPropertyChanged(BR.genreId)
        }
}
