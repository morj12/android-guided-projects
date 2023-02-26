package com.example.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShopItemDao {

    @Query("SELECT * FROM shop_item")
    fun getShopItems(): LiveData<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShopItem(shopItemDbModel: ShopItemDbModel)

    @Query("DELETE FROM shop_item WHERE id = :id")
    suspend fun deleteShopItem(id: Int)

    @Query("SELECT * FROM shop_item WHERE ID = :id LIMIT 1")
    suspend fun getShopItem(id: Int): ShopItemDbModel
}