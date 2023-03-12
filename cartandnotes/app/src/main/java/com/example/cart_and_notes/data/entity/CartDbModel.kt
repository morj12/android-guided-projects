package com.example.cart_and_notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartDbModel(
    val name: String,
    val creationTime: String,
    val totalItems: Int,
    val checkedItems: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
