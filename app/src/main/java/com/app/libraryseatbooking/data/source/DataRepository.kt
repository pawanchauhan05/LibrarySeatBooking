package com.app.libraryseatbooking.data.source

import com.app.libraryseatbooking.R
import com.app.libraryseatbooking.core.AppController
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import com.app.libraryseatbooking.data.source.remote.IRemoteDataSource
import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import com.app.libraryseatbooking.utilities.Utility
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class DataRepository(
    public val localDataSource: ILocalDataSource,
    public val remoteDataSource: IRemoteDataSource,
    public val dispatcher: CoroutineDispatcher
) : IDataRepository {

    override suspend fun getOnGoingSessionQRCodeData(): Flow<ResultState> = flow {
        val qrCodeData = localDataSource.getQRCodeData()
        if (qrCodeData == null) {
            emit(ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button.") as ResultState)
        } else {
            emit(ResultState.StartSession(qrCodeData) as ResultState)
        }
    }

    override suspend fun startOrStopSession(
        qrCodeData: QRCodeData,
        currentTime: Long
    ): Flow<ResultState> = flow {
        try {
            val count = localDataSource.getQRCodeDataCountByLocationId(qrCodeData.location_id)
            if (count == 0) {
                // start session
                localDataSource.insertQRCodeData(qrCodeData)
                val qrCodeData = localDataSource.getQRCodeDataByLocationId(qrCodeData.location_id)
                emit(ResultState.StartSession(qrCodeData) as ResultState)
            } else {
                // end session
                // get data from local, post to remote server and clear from local then update UI
                val qrCodeData = localDataSource.getQRCodeDataByLocationId(qrCodeData.location_id)
                val minutes = Utility.getMinBetweenDates(
                    qrCodeData.sessionEndTime,
                    currentTime
                )
                val queryParams = mutableMapOf<String, String>().apply {
                    "location_id" to qrCodeData.location_id
                    "time_spent" to minutes.toString()
                    "end_time" to currentTime.toString()
                }
                val serverResponse = remoteDataSource.submitSession(queryParams).also {
                    localDataSource.updateQRCodeDataEndTime(qrCodeData.location_id, currentTime)
                    localDataSource.deleteQRCodeDataByLocationId(qrCodeData.location_id)
                }
                emit(ResultState.Success(serverResponse) as ResultState)
                emit(ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button.") as ResultState)
            }
        } catch (ex: Exception) {
            emit(ResultState.Failure(ex) as ResultState)
        }
    }

    override suspend fun getOnGoingSessionTimeInMinutes(timeStamp: Long): Long {
        val qrCodeData = localDataSource.getQRCodeData()
        return if (qrCodeData == null) {
            0
        } else {
            Utility.getMinBetweenDates(
                endTime = timeStamp,
                startTime = qrCodeData.sessionStartTime
            )
        }
    }


}