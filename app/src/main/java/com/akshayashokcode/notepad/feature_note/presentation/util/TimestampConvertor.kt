package com.akshayashokcode.notepad.feature_note.presentation.util

import java.text.SimpleDateFormat
import java.util.*

class TimestampConvertor {
    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getDateTime(value: Long): String {
        val formatDate = "dd MMM yyyy"
        val formatTime = "HH:mm"
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat(formatDate, Locale.getDefault()) // default local
        val sdfTime = SimpleDateFormat(formatTime, Locale.getDefault()) // default local

        return if(sdf.format(Date(value)).equals(sdf.format(Date(now)))){
            sdfTime.format(Date(value))
        } else{
            sdf.format(Date(value))
            }
    }

    fun getTimeAgo(time: Long): String {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return "1"
        }
        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                diff.div(MINUTE_MILLIS).toString() + " minutes ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                diff.div(HOUR_MILLIS).toString() + " hours ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            diff < 3 * DAY_MILLIS -> {
                diff.div(DAY_MILLIS).toString() + " days ago"
            }
            else -> {
                "1"
            }
        }
    }
}