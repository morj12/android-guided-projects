package com.example.wildrunning.utils

object TimeFormatter {

    fun timeToSeconds(hh: Int, mm: Int, ss: Int): Int {
        var hours = hh.toString()
        if (hh < 10) hours = "0$hours"
        var minutes = mm.toString()
        if (mm < 10) minutes = "0$minutes"
        var seconds = ss.toString()
        if (ss < 10) seconds = "0$seconds"

        return timeToSeconds("$hours:$minutes:$seconds")
    }

    fun timeToSeconds(timeString: String): Int {
        var seconds = 0
        var w = timeString
        if (w.length == 5) w = "00:$w"

        seconds += w.subSequence(0, 2).toString().toInt() * 3600
        seconds += w.subSequence(3, 5).toString().toInt() * 60
        seconds += w.subSequence(6, 8).toString().toInt()

        return seconds
    }

    fun secondsToString(seconds: Long): String {
        return String.format(
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % 3600) / 60,
            (seconds % 60)
        )
    }

    fun secondsToStringWithDate(seconds: Long): String {
        var res = ""
        var secs = seconds
        val years = (secs / 31536000).toInt()
        secs %= 31536000
        val months = (secs / 2592000).toInt()
        secs %= 2592000
        val days = (secs / 86400).toInt()
        secs %= 86400

        if (years > 0) res += "${years}y "
        if (months > 0) res += "${months}m "
        if (days > 0) res += "${days}d "

        return res + secondsToString(secs)

    }

}