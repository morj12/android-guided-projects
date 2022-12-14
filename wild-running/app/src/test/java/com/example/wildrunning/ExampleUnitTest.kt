package com.example.wildrunning

import com.example.wildrunning.utils.EmailValidator
import com.example.wildrunning.utils.NumberHelper
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun time_IsCorrect() {
        val seconds = 7450
        val timeString = "02:04:10"

        val result = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60))
        assertEquals(timeString, result)
    }

    @Test
    fun round_IsCorrect() {
        val value = 3.1401591023
        val rounded = "3.14"

        val result = NumberHelper.round(value, 2)
        assertEquals(rounded, result)
    }

    @Test
    fun emailMatcher_IsCorrect() {
        val emails = listOf("a", "a.com", "@com", ".@com", "a@.com", "a@com", "a@a.com", "a@a.com.")
        val expected = listOf(false, false, false, false, false, false, true, false)

        val result = emails.map(EmailValidator::isEmail)
        assertEquals(expected, result)
    }
}