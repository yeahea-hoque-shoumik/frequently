package com.prime.frequently.utils

object TimeUtils {

    fun secondsToMmSs(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    fun minutesToSeconds(minutes: Int): Int = minutes * 60

    fun millisToSeconds(millis: Long): Int = (millis / 1000).toInt()
}
