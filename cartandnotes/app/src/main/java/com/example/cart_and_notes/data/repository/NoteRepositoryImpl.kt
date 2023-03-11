package com.example.cart_and_notes.data.repository

import com.example.cart_and_notes.data.db.AppDatabase
import com.example.cart_and_notes.domain.entity.Note
import com.example.cart_and_notes.domain.mapper.Mapper
import com.example.cart_and_notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(database: AppDatabase): NoteRepository {

    private val dao = database.noteDao()

    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes().map(Mapper::mapNoteListToEntity)
    }

    override suspend fun insertNote(note: Note) {
        dao.insertNote(Mapper.mapNoteToDbModel(note))
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(Mapper.mapNoteToDbModel(note))
    }

    override suspend fun updateNote(note: Note) {
        dao.updateNote(Mapper.mapNoteToDbModel(note))
    }
}