package com.example.cart_and_notes.util

import android.content.SharedPreferences
import com.example.cart_and_notes.R

object PrefsTheme {

    fun getMainTheme(prefs: SharedPreferences) =
        if (prefs.getString(
                PrefsConsts.Keys.THEME_KEY,
                PrefsConsts.Values.BLUE_THEME
            ) == PrefsConsts.Values.BLUE_THEME
        ) {
            R.style.Theme_CartandnotesBlue
        } else R.style.Theme_CartandnotesRed

    fun getNewNoteTheme(prefs: SharedPreferences) =
        if (prefs.getString(
                PrefsConsts.Keys.THEME_KEY,
                PrefsConsts.Values.BLUE_THEME
            ) == PrefsConsts.Values.BLUE_THEME
        ) {
            R.style.Theme_NewNoteBlue
        } else R.style.Theme_NewNoteRed
}