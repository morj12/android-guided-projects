package com.example.cart_and_notes.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cart_and_notes.entity.CardItemDbModel
import com.example.cart_and_notes.entity.CardListDbModel
import com.example.cart_and_notes.entity.DbItemDbModel
import com.example.cart_and_notes.entity.NoteDbModel

@Database(
    entities = [CardItemDbModel::class, CardListDbModel::class, DbItemDbModel::class, NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        private const val DB_NAME = "card_and_notes"
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(application: Application): AppDatabase {
            INSTANCE?.let { return it }
            synchronized(this) {
                INSTANCE?.let { return it }
                val db = Room.databaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = db
                return db
            }
        }
    }
}