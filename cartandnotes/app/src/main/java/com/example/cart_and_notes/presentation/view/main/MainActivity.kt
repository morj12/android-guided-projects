package com.example.cart_and_notes.presentation.view.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.example.cart_and_notes.R
import com.example.cart_and_notes.databinding.ActivityMainBinding
import com.example.cart_and_notes.presentation.view.FragmentManager
import com.example.cart_and_notes.presentation.view.settings.SettingsActivity
import com.example.cart_and_notes.util.PrefsConsts.Keys.THEME_KEY
import com.example.cart_and_notes.util.PrefsConsts.Values.BLUE_THEME
import com.example.cart_and_notes.util.PrefsTheme.getMainTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentTab = R.id.notes
    private var currentTheme = ""

    private lateinit var prefs: SharedPreferences

    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initPrefs()
        setTheme(getMainTheme(prefs))
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initBottomNavigation()
//        loadNewAd()
    }

    private fun initPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = prefs.getString(THEME_KEY, BLUE_THEME).toString()
    }

    private fun initBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.setting -> {
//                    showAd(object : AdListener {
//                        override fun onFinish() {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
//                        }
//                    })

                }
                R.id.notes -> {
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                    currentTab = R.id.notes
                }
                R.id.cart -> {
                    FragmentManager.setFragment(CartFragment.newInstance(), this)
                    currentTab = R.id.cart
                }
            }
            true
        }
    }

    private fun loadNewAd() {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstitial_ad_id),
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    private fun showAd(adListener: AdListener) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadNewAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadNewAd()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                    loadNewAd()
                }
            }
            interstitialAd?.show(this)
        } else {
            adListener.onFinish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_any_item -> FragmentManager.currentFragment?.onClickNew()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNav.selectedItemId = currentTab
        if (prefs.getString(THEME_KEY, BLUE_THEME) != currentTheme) recreate()
    }

    interface AdListener {
        fun onFinish()
    }
}