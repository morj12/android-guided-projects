package com.example.wildrunning.utils

object NumberHelper {

    fun round(number: Double, decimals: Int = 2): String = "%.${decimals}f"
        .format(number)
        .replace(",", ".")
}