package com.example.shoppinglist.data

import com.example.shoppinglist.domain.ShopItem

class ShopItemMapper {

    fun mapEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        shopItem.id,
        shopItem.name,
        shopItem.count,
        shopItem.enabled
    )

    fun mapDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        shopItemDbModel.name,
        shopItemDbModel.count,
        shopItemDbModel.enabled,
        shopItemDbModel.id,
    )

    fun mapListDbModelToListEntity(listDbModel: List<ShopItemDbModel>) =
        listDbModel.map(::mapDbModelToEntity)

}