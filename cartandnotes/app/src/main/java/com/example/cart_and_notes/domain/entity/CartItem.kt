package com.example.cart_and_notes.domain.entity

data class CartItem(
    val name: String,
    val info: String,
    val checked: Boolean,
    val listId: Int,
    val itemType: String = "item",
    val id: Int = 0
)
