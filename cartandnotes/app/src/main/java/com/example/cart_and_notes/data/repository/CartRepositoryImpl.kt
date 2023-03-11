package com.example.cart_and_notes.data.repository

import com.example.cart_and_notes.data.db.AppDatabase
import com.example.cart_and_notes.domain.entity.Cart
import com.example.cart_and_notes.domain.mapper.Mapper
import com.example.cart_and_notes.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(database: AppDatabase): CartRepository {

    private val dao = database.cartDao()

    override fun getCarts(): Flow<List<Cart>> {
        return dao.getCarts().map(Mapper::mapCartListToEntity)
    }

    override suspend fun insertCart(cart: Cart) {
        dao.insertCart(Mapper.mapCartToDbModel(cart))
    }

    override suspend fun deleteCart(cart: Cart) {
        dao.deleteCart(Mapper.mapCartToDbModel(cart))
    }

    override suspend fun updateCart(cart: Cart) {
        dao.updateCart(Mapper.mapCartToDbModel(cart))
    }
}