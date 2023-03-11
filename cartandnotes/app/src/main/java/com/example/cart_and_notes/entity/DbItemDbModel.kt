package com.example.cart_and_notes.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "db_item")
data class DbItemDbModel(
    val name: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
