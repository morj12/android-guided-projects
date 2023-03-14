package com.example.cart_and_notes.util

import android.widget.EditText
import android.widget.TextView

fun TextView.setTextSize(size: String?) {
    if (size != null) this.textSize = size.toFloat()
}

fun EditText.setTextSize(size: String?) {
    if (size != null) this.textSize = size.toFloat()
}