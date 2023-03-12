package com.example.cart_and_notes.presentation.view.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.cart_and_notes.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_pref, rootKey)
    }


}