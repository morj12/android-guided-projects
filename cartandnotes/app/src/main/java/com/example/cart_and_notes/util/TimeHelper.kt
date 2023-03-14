package com.example.cart_and_notes.util

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import com.example.cart_and_notes.util.PrefsConsts.Keys.TIME_FORMAT_KEY
import java.util.*

object TimeHelper {

    private const val DEFAULT_TIME_FORMAT = "hh:mm:ss - yyyy/MM/dd"

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, prefs: SharedPreferences): String {
        val formatter = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault())
        val date = formatter.parse(time)
        val newFormat = prefs.getString(TIME_FORMAT_KEY, DEFAULT_TIME_FORMAT)
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())
        return if (date != null) newFormatter.format(date) else time
    }

}