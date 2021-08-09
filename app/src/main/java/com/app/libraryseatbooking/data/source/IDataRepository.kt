package com.app.libraryseatbooking.data.source

import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import kotlinx.coroutines.flow.Flow

interface IDataRepository {

    suspend fun getOnGoingSessionQRCodeData() : Flow<ResultState>

    suspend fun startOrStopSession(qrCodeData: QRCodeData, currentTime : Long) : Flow<ResultState>

    suspend fun getOnGoingSessionTimeInMinutes(timeStamp: Long) : Long

}