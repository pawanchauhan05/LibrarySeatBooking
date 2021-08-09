package com.app.libraryseatbooking.data.source.local

import com.app.libraryseatbooking.pojo.QRCodeData

class FakeLocalDataSource(private var qrCodeDataList: MutableList<QRCodeData> = mutableListOf()) : ILocalDataSource {

    override fun insertQRCodeData(qrCodeData: QRCodeData) {
        qrCodeDataList.add(qrCodeData)
    }

    override fun getQRCodeData(): QRCodeData? {
        if(qrCodeDataList.isNotEmpty()) {
            return qrCodeDataList[0]
        }
        return null
    }

    override fun getQRCodeDataByLocationId(locationId: String): QRCodeData {
        return qrCodeDataList?.first {
            it.location_id == locationId
        }
    }

    override fun getQRCodeDataCountByLocationId(locationId: String): Int {
        return qrCodeDataList.count {
            it.location_id == locationId
        }
    }

    override fun deleteQRCodeDataByLocationId(locationId: String) {
        qrCodeDataList.removeIf { it.location_id == locationId }
    }

    override fun updateQRCodeDataEndTime(locationId: String, endTime: Long) {
        qrCodeDataList.first {
            it.location_id == locationId
        }.sessionEndTime = endTime
    }

    override fun updateQRCodeDataSyncStatus(locationId: String, syncStatus: Boolean) {
        qrCodeDataList.first {
            it.location_id == locationId
        }.dataSync = syncStatus
    }
}