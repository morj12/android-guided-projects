package com.example.cart_and_notes.data.repository

import com.example.cart_and_notes.data.db.AppDatabase
import com.example.cart_and_notes.domain.entity.CartItem
import com.example.cart_and_notes.domain.mapper.Mapper
import com.example.cart_and_notes.domain.repository.CartItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartItemRepositoryImpl(database: AppDatabase): CartItemRepository {

    private val dao = database.cartItemDao()

    override fun getCartItems(cartId: Int): Flow<List<CartItem>> {
        return dao.getCartItems(cartId).map(Mapper::mapCartItemListToEntity)
    }

    override fun getCartItems(): Flow<List<CartItem>> {
        return dao.getCartItems().map(Mapper::mapCartItemListToEntity)
    }

    override suspend fun insertCartItem(cartItem: CartItem) {
        dao.insertCartItem(Mapper.mapCartItemToDbModel(cartItem))
    }

    override suspend fun deleteCartItem(cartItem: CartItem) {
        dao.deleteCartItem(Mapper.mapCartItemToDbModel(cartItem))
    }

    override suspend fun updateCartItem(cartItem: CartItem) {
        dao.updateCartItem(Mapper.mapCartItemToDbModel(cartItem))
    }

    override suspend fun deleteCartItems(cartId: Int) {
        dao.deleteCartItems(cartId)
    }
}