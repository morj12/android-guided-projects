package com.example.cart_and_notes.domain.repository

import com.example.cart_and_notes.domain.entity.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getCarts(): Flow<List<Cart>>

    suspend fun insertCart(cart: Cart)

    suspend fun deleteCart(cart: Cart)

    suspend fun updateCart(cart: Cart)
}