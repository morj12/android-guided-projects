package com.example.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import kotlin.random.Random

object ShopListRepositoryImpl : ShopListRepository {

    private val shopListLD = MutableLiveData<List<ShopItem>>()
    private val shopList = mutableListOf<ShopItem>()

    private var autoIncrementId = 0

    init {
        (0..15).forEach {
            val item = ShopItem("Name $it", it * 2, Random.nextBoolean())
            addShopItem(item)
        }
    }

    override fun getShopList() = shopListLD

    override fun getShopItem(id: Int) = shopList.find { it.id == id }
        ?: throw RuntimeException("Elements with id $id not found")

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) shopItem.id = autoIncrementId++
        shopList.add(shopItem)
        updateList()
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        shopList[shopList.indexOf(shopList.find { it.id == shopItem.id })] = shopItem
        updateList()
    }

    private fun updateList() {
        shopListLD.value = shopList.toList()
    }
}