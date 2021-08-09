package com.app.libraryseatbooking.data.source.local

import com.app.libraryseatbooking.data.source.local.dao.QRCodeDataDao
import com.app.libraryseatbooking.pojo.QRCodeData
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

class LocalDataSource internal constructor(
    private val qrCodeDataDao: QRCodeDataDao,
    private val dispatcher: CoroutineDispatcher
)  : ILocalDataSource {
    override fun insertQRCodeData(qrCodeData: QRCodeData) {
        qrCodeDataDao.insertQRCodeData(qrCodeData)
    }

    override fun getQRCodeData(): QRCodeData {
        return qrCodeDataDao.getQRCodeData()
    }

    override fun getQRCodeDataByLocationId(locationId: String): QRCodeData {
        return qrCodeDataDao.getQRCodeDataByLocationId(locationId)
    }

    override fun getQRCodeDataCountByLocationId(locationId: String): Int {
        return qrCodeDataDao.getQRCodeDataCountByLocationId(locationId)
    }

    override fun deleteQRCodeDataByLocationId(locationId: String) {
        qrCodeDataDao.deleteQRCodeDataByLocationId(locationId)
    }

    override fun updateQRCodeDataEndTime(locationId: String, endTime : Long) {
        val qrCodeData = getQRCodeDataByLocationId(locationId)
        qrCodeData.sessionEndTime = endTime
        insertQRCodeData(qrCodeData)
    }

    override fun updateQRCodeDataSyncStatus(locationId: String, syncStatus: Boolean) {
        val qrCodeData = getQRCodeDataByLocationId(locationId)
        qrCodeData.dataSync = true
        insertQRCodeData(qrCodeData)
        deleteQRCodeDataByLocationId(locationId)
    }
}