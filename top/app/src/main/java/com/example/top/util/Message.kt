package com.example.top.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

object Message {

    fun showMessage(view: View, message: Int) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}