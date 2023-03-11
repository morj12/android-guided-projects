package com.example.cart_and_notes.domain.mapper

import com.example.cart_and_notes.data.entity.CartDbModel
import com.example.cart_and_notes.data.entity.NoteDbModel
import com.example.cart_and_notes.domain.entity.Cart
import com.example.cart_and_notes.domain.entity.Note

object Mapper {

    fun mapCartToDbModel(cart: Cart) = CartDbModel(
        cart.name,
        cart.creationTime,
        cart.totalItems,
        cart.checkedItems,
        cart.itemIds,
        cart.id
    )

    fun mapCartToEntity(cartDbModel: CartDbModel) = Cart(
        cartDbModel.name,
        cartDbModel.creationTime,
        cartDbModel.totalItems,
        cartDbModel.checkedItems,
        cartDbModel.itemIds,
        cartDbModel.id
    )

    fun mapNoteToDbModel(note: Note) = NoteDbModel(
        note.title,
        note.content,
        note.time,
        note.category,
        note.id
    )

    fun mapNoteToEntity(noteDbModel: NoteDbModel) = Note(
        noteDbModel.title,
        noteDbModel.content,
        noteDbModel.time,
        noteDbModel.category,
        noteDbModel.id
    )

    fun mapCartListToDbModel(noteList: List<Cart>) =
        noteList.map(::mapCartToDbModel)

    fun mapCartListToEntity(cartDbModelList: List<CartDbModel>) =
        cartDbModelList.map(::mapCartToEntity)

    fun mapNoteListToDbModel(noteList: List<Note>) =
        noteList.map(::mapNoteToDbModel)

    fun mapNoteListToEntity(noteDbModelList: List<NoteDbModel>) =
        noteDbModelList.map(::mapNoteToEntity)

}