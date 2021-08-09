package com.app.libraryseatbooking.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.libraryseatbooking.data.source.local.dao.QRCodeDataDao
import com.app.libraryseatbooking.pojo.QRCodeData
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var qrCodeDataDao: QRCodeDataDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        qrCodeDataDao = appDatabase.getQRCodeDataDao()
    }

    @Test
    fun insertAndReadQRCodeData() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            Calendar.getInstance().timeInMillis
        )
        qrCodeDataDao.insertQRCodeData(qrCodeData)
        val dbQrCodeData = qrCodeDataDao.getQRCodeData()
        Assert.assertEquals(qrCodeData, dbQrCodeData)
    }

    @Test
    fun insertAndReadQRCodeDataByLocationId() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            Calendar.getInstance().timeInMillis
        )
        qrCodeDataDao.insertQRCodeData(qrCodeData)

        val dbQrCodeData = qrCodeDataDao.getQRCodeDataByLocationId("ButterKnifeLib-1234")

        Assert.assertEquals(qrCodeData, dbQrCodeData)

    }

    @Test
    fun insertAndReadCountQRCodeDataByLocationId() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            Calendar.getInstance().timeInMillis
        )
        qrCodeDataDao.insertQRCodeData(qrCodeData)
        val count = qrCodeDataDao.getQRCodeDataCountByLocationId("ButterKnifeLib-1234")
        Assert.assertEquals(count, 1)
    }

    @Test
    fun insertAndDeleteQRCodeDataByLocationId() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            Calendar.getInstance().timeInMillis
        )
        qrCodeDataDao.insertQRCodeData(qrCodeData)
        qrCodeDataDao.deleteQRCodeDataByLocationId("ButterKnifeLib-1234")
        val dbQrCodeData = qrCodeDataDao.getQRCodeData()
        Assert.assertNull(dbQrCodeData)
    }

    @Test
    fun insertAndUpdateQRCodeDataEndTimeByLocationId() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = Calendar.getInstance().timeInMillis
        )

        qrCodeDataDao.insertQRCodeData(qrCodeData)
        val dbQrCodeData = qrCodeDataDao.getQRCodeDataByLocationId("ButterKnifeLib-1234")

        dbQrCodeData.sessionEndTime = 1628346153
        qrCodeDataDao.insertQRCodeData(dbQrCodeData)

        val dbQrCodeData1 = qrCodeDataDao.getQRCodeDataByLocationId("ButterKnifeLib-1234")
        Assert.assertEquals(dbQrCodeData1.sessionEndTime, 1628346153)
    }

    @Test
    fun insertAndUpdateQRCodeDataSyncStatusByLocationId() {
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            Calendar.getInstance().timeInMillis,
            sessionEndTime = Calendar.getInstance().timeInMillis
        )
        qrCodeDataDao.insertQRCodeData(qrCodeData)
        val dbQrCodeData = qrCodeDataDao.getQRCodeDataByLocationId("ButterKnifeLib-1234")

        dbQrCodeData.dataSync = true
        qrCodeDataDao.insertQRCodeData(dbQrCodeData)
        val dbQrCodeData1 = qrCodeDataDao.getQRCodeDataByLocationId("ButterKnifeLib-1234")

        Assert.assertEquals(dbQrCodeData1.dataSync, true)
    }


    @After
    fun closeDb() {
        appDatabase.close()
    }
}