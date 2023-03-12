package com.example.cart_and_notes.domain.repository

import com.example.cart_and_notes.domain.entity.CartItem
import kotlinx.coroutines.flow.Flow

interface CartItemRepository {

    fun getCartItems(cartId: Int): Flow<List<CartItem>>

    fun getCartItems(): Flow<List<CartItem>>

    suspend fun insertCartItem(cartItem: CartItem)

    suspend fun deleteCartItem(cartItem: CartItem)

    suspend fun updateCartItem(cartItem: CartItem)

    suspend fun deleteCartItems(cartId: Int)

}