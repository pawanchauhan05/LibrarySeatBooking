package com.app.libraryseatbooking.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.libraryseatbooking.data.source.local.converters.DateConverter
import com.app.libraryseatbooking.data.source.local.dao.QRCodeDataDao
import com.app.libraryseatbooking.pojo.QRCodeData

@Database(entities = [QRCodeData::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getQRCodeDataDao(): QRCodeDataDao
}