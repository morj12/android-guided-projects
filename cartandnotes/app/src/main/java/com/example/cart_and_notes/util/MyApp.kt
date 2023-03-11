package com.example.cart_and_notes.util

import android.app.Application
import com.example.cart_and_notes.db.AppDatabase

class MyApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}