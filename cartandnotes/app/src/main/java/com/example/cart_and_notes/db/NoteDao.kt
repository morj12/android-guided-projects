package com.example.cart_and_notes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cart_and_notes.entity.NoteDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<NoteDbModel>>

    @Insert
    suspend fun insertNote(note: NoteDbModel)

    @Delete
    suspend fun deleteNote(note: NoteDbModel)

    @Update
    suspend fun updateNote(note: NoteDbModel)
}