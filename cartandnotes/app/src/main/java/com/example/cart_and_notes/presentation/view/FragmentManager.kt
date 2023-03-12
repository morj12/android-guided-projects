package com.example.cart_and_notes.presentation.view

import androidx.appcompat.app.AppCompatActivity
import com.example.cart_and_notes.R

object FragmentManager {

    var currentFragment: AdditionFragment? = null

    fun setFragment(newFragment: AdditionFragment, activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, newFragment)
            .commit()

        currentFragment = newFragment
    }

}