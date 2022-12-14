package com.example.wildrunning.utils

import androidx.core.util.PatternsCompat

object EmailValidator {

    fun isEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

}