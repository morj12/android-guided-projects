package com.example.cart_and_notes

import android.app.Application
import com.example.cart_and_notes.data.db.AppDatabase

class MyApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}