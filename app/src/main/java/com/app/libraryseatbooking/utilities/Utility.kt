package com.app.libraryseatbooking.utilities

import java.text.SimpleDateFormat

object Utility {

    fun getMinBetweenDates(endTime : Long, startTime : Long) : Long {
        val diff = endTime - startTime
        return (diff / (1000 * 60))
    }

    fun getFormattedTime(pattern : String, timestamp : Long) : String {
        return SimpleDateFormat(pattern).format(timestamp)
    }
}