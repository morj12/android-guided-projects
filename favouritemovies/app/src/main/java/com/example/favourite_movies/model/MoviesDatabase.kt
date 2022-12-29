package com.example.favourite_movies.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(entities = [Genre::class, Movie::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun genreDao(): GenreDao

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MoviesDatabase? = null

        fun getDatabase(context: Context): MoviesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoviesDatabase::class.java, "top_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                BasicData.load(context)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}