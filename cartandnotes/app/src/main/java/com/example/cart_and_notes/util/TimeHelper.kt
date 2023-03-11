package com.example.cart_and_notes.util

import android.icu.text.SimpleDateFormat
import java.util.*

object TimeHelper {

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("hh:mm:ss - yyyy/MM/dd", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

}