package com.app.libraryseatbooking.data.source.local.dao

import androidx.room.*
import com.app.libraryseatbooking.pojo.QRCodeData

@Dao
interface QRCodeDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQRCodeData(qrCodeData: QRCodeData)

    @Query("SELECT * FROM QRCodeData LIMIT 1")
    fun getQRCodeData(): QRCodeData

    @Query("SELECT * FROM QRCodeData WHERE location_id = :locationId LIMIT 1")
    fun getQRCodeDataByLocationId(locationId : String): QRCodeData

    @Query("SELECT COUNT(*) FROM QRCodeData WHERE location_id = :locationId")
    fun getQRCodeDataCountByLocationId(locationId: String) : Int

    @Query("DELETE FROM QRCodeData WHERE location_id = :locationId")
    fun deleteQRCodeDataByLocationId(locationId: String)
}