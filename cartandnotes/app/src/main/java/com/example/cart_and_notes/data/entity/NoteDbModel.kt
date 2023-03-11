package com.example.cart_and_notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteDbModel(
    val title: String,
    val content: String,
    val time: String,
    val category: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
