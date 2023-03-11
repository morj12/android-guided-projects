package com.example.cart_and_notes.presentation.view.main

import androidx.lifecycle.*
import com.example.cart_and_notes.db.AppDatabase
import com.example.cart_and_notes.entity.NoteDbModel
import kotlinx.coroutines.launch

class MainViewModel(database: AppDatabase): ViewModel() {

    private val noteDao = database.noteDao()

    val allNotes: LiveData<List<NoteDbModel>> = noteDao.getNotes().asLiveData()

    fun insertNote(note: NoteDbModel) = viewModelScope.launch {
        noteDao.insertNote(note)
    }

    fun deleteNote(note: NoteDbModel) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    fun updateNote(note: NoteDbModel) = viewModelScope.launch {
        noteDao.updateNote(note)
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