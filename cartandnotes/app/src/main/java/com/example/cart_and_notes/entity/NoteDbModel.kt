package com.example.cart_and_notes.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note")
data class NoteDbModel(
    val title: String,
    val content: String,
    val time: String,
    val category: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) : Parcelable
