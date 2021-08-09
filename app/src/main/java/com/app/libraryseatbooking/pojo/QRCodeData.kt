package com.app.libraryseatbooking.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.libraryseatbooking.data.source.local.converters.DateConverter

@Entity
data class QRCodeData(
    @PrimaryKey @ColumnInfo(name = "location_id") val location_id: String,
    @ColumnInfo(name = "location_detail") val location_detail: String,
    @ColumnInfo(name = "price_per_min") val price_per_min: Float,
    @ColumnInfo(name = "start_time") @TypeConverters(DateConverter::class) var sessionStartTime: Long,
    @ColumnInfo(name = "end_time") @TypeConverters(DateConverter::class) var sessionEndTime: Long = 0,
    @ColumnInfo(name = "data_sync") var dataSync : Boolean = false
)