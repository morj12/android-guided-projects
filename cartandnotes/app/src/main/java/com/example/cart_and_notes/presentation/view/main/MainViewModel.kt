package com.example.cart_and_notes.presentation.view.main

import androidx.lifecycle.*
import com.example.cart_and_notes.data.db.AppDatabase
import com.example.cart_and_notes.data.repository.CartItemRepositoryImpl
import com.example.cart_and_notes.data.repository.CartRepositoryImpl
import com.example.cart_and_notes.data.repository.NoteRepositoryImpl
import com.example.cart_and_notes.domain.entity.Cart
import com.example.cart_and_notes.domain.entity.CartItem
import com.example.cart_and_notes.domain.entity.Note
import kotlinx.coroutines.launch

// TODO: remove dependencies from data layer using Dagger
class MainViewModel(val database: AppDatabase) : ViewModel() {

    private val noteRepository = NoteRepositoryImpl(database)
    private val cartRepository = CartRepositoryImpl(database)
    private val cartItemRepository = CartItemRepositoryImpl(database)

    val allNotes: LiveData<List<Note>> = noteRepository.getNotes().asLiveData()

    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    val allCarts: LiveData<List<Cart>> = cartRepository.getCarts().asLiveData()

    fun insertCart(cart: Cart) = viewModelScope.launch {
        cartRepository.insertCart(cart)
    }

    fun deleteCart(cart: Cart) = viewModelScope.launch {
        cartRepository.deleteCart(cart)
    }

    fun updateCart(cart: Cart) = viewModelScope.launch {
        cartRepository.updateCart(cart)
    }

    fun allCartItems(cartId: Int) = cartItemRepository.getCartItems(cartId).asLiveData()

    val allCartItems: LiveData<List<CartItem>> = cartItemRepository.getCartItems().asLiveData()

    fun insertCartItem(cartItem: CartItem) = viewModelScope.launch {
        cartItemRepository.insertCartItem(cartItem)
    }

    fun deleteCartItem(cartItem: CartItem) = viewModelScope.launch {
        cartItemRepository.deleteCartItem(cartItem)
    }

    fun updateCartItem(cartItem: CartItem) = viewModelScope.launch {
        cartItemRepository.updateCartItem(cartItem)
    }

    fun clearCart(cartId: Int) = viewModelScope.launch {
        cartItemRepository.deleteCartItems(cartId)
    }

    class MainViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown view model class")
        }
    }
}