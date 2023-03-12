package com.example.cart_and_notes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cart_and_notes.data.entity.CartItemDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_item WHERE listId = :cartId")
    fun getCartItems(cartId: Int): Flow<List<CartItemDbModel>>

    @Query("SELECT * FROM cart_item")
    fun getCartItems(): Flow<List<CartItemDbModel>>

    @Insert
    suspend fun insertCartItem(cartItem: CartItemDbModel)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemDbModel)

    @Update
    suspend fun updateCartItem(cartItem: CartItemDbModel)

    @Query("DELETE FROM cart_item WHERE listId = :cartId")
    suspend fun deleteCartItems(cartId: Int)
}