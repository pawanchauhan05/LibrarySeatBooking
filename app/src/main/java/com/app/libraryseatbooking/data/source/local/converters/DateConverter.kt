package com.app.libraryseatbooking.data.source.local.converters

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun timeToDate(timeInMillis: Long): Date {
        return Date(timeInMillis)
    }

    @TypeConverter
    fun dateToTime(value: Date): Long {
        return value.time
    }
}