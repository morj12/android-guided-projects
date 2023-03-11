package com.example.cart_and_notes.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_item")
data class CardItemDbModel(
    val name: String,
    val info: String,
    val checked: Boolean,
    val listId: Int,
    val itemType: String = "item",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
