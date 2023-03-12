package com.example.cart_and_notes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_item",
    foreignKeys = [ForeignKey(
        entity = CartDbModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listId"),
        onDelete = CASCADE
    )]
)
data class CartItemDbModel(
    val name: String,
    val info: String,
    val checked: Boolean,
    val listId: Int,
    val itemType: String = "item",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
