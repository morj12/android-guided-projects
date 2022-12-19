package com.example.top.util

import com.google.android.material.textfield.TextInputEditText

object ArtistValidator {

    fun validate(
        heightView: TextInputEditText,
        heightErrorMessage: String,
        surnameView: TextInputEditText,
        surnameErrorMessage: String,
        nameView: TextInputEditText,
        nameErrorMessage: String
    ): Boolean {
        var isValid = true

        val height = heightView.text.toString()

        val incorrectHeight = height.isEmpty()      // not null
                || height.any { !it.isDigit() }     // not number
                || height.toInt() <= 0              // negative number

        if (incorrectHeight) {
            heightView.error = heightErrorMessage
            heightView.requestFocus()
            isValid = false
        }

        val nullSurname = surnameView.text.toString().trim().isEmpty()

        if (nullSurname) {
            surnameView.error = surnameErrorMessage
            surnameView.requestFocus()
            isValid = false
        }

        val nullName = nameView.text.toString().trim().isEmpty()

        if (nullName) {
            nameView.error = nameErrorMessage
            nameView.requestFocus()
            isValid = false
        }

        return isValid
    }
}