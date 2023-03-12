package com.example.cart_and_notes.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cart_and_notes.data.dao.CartDao
import com.example.cart_and_notes.data.dao.CartItemDao
import com.example.cart_and_notes.data.dao.NoteDao
import com.example.cart_and_notes.data.entity.CartItemDbModel
import com.example.cart_and_notes.data.entity.CartDbModel
import com.example.cart_and_notes.data.entity.NoteDbModel

@Database(
    entities = [CartItemDbModel::class, CartDbModel::class, NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemDao(): CartItemDao

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