package com.example.cart_and_notes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cart_and_notes.data.entity.CartDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart")
    fun getCarts(): Flow<List<CartDbModel>>

    @Insert
    suspend fun insertCart(cart: CartDbModel)

    @Delete
    suspend fun deleteCart(cart: CartDbModel)

    @Update
    suspend fun updateCart(cart: CartDbModel)
}