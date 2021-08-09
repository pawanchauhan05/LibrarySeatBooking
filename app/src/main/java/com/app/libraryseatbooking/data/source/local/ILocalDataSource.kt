package com.app.libraryseatbooking.data.source.local

import com.app.libraryseatbooking.pojo.QRCodeData

interface ILocalDataSource {
    fun insertQRCodeData(qrCodeData: QRCodeData)

    fun getQRCodeData(): QRCodeData?

    fun getQRCodeDataByLocationId(locationId: String): QRCodeData

    fun getQRCodeDataCountByLocationId(locationId: String) : Int

    fun deleteQRCodeDataByLocationId(locationId: String)

    fun updateQRCodeDataEndTime(locationId: String, endTime : Long)

    fun updateQRCodeDataSyncStatus(locationId: String, syncStatus : Boolean)
}