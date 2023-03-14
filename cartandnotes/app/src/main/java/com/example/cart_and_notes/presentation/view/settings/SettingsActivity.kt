package com.example.cart_and_notes.presentation.view.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivitySettingsBinding
import com.example.cart_and_notes.util.PrefsConsts.Keys.THEME_KEY
import com.example.cart_and_notes.util.PrefsTheme.getMainTheme

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var prefs: SharedPreferences

    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        initPrefs()
        setTheme(getMainTheme(prefs))
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            settingsFragment = SettingsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_placeholder, settingsFragment)
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}