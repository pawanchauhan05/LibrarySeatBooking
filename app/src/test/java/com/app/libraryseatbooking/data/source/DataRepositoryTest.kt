package com.app.libraryseatbooking.data.source

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.libraryseatbooking.FakeResponseUtility
import com.app.libraryseatbooking.MainCoroutineRule
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import com.app.libraryseatbooking.data.source.remote.FakeRemoteDataSource
import com.app.libraryseatbooking.data.source.remote.IRemoteDataSource
import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class DataRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fakeLocalDataSource: ILocalDataSource

    @Inject
    lateinit var fakeRemoteDataSource: IRemoteDataSource

    @Inject
    lateinit var coDispatcher: CoroutineDispatcher

    private lateinit var dataRepository: DataRepository

    @Before
    fun setUp() {
        // Populate @Inject fields in test class
        hiltRule.inject()
        dataRepository = DataRepository(fakeLocalDataSource, fakeRemoteDataSource, coDispatcher)
    }

    @Test
    fun getOnGoingSessionTimeInMinutes_shouldReturn0Minute() = mainCoroutineRule.runBlockingTest {
        val startTime = 1628349316000L
        val endTime = 1628349330000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        fakeLocalDataSource.insertQRCodeData(qrCodeData)
        Assert.assertEquals(0, dataRepository.getOnGoingSessionTimeInMinutes(endTime))
    }

    @Test
    fun getOnGoingSessionTimeInMinutes_shouldReturn2Minute() = mainCoroutineRule.runBlockingTest {
        val startTime = 1628349527000L
        val endTime = 1628349661000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        fakeLocalDataSource.insertQRCodeData(qrCodeData)
        Assert.assertEquals(2, dataRepository.getOnGoingSessionTimeInMinutes(endTime))
    }

    @Test
    fun getOnGoingSessionQRCodeData_shouldReturn_NoSession() = mainCoroutineRule.runBlockingTest {
        val list = dataRepository.getOnGoingSessionQRCodeData().toList()

        Assert.assertEquals(
            list, listOf(
                ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button.")
            )
        )
    }

    @Test
    fun getOnGoingSessionQRCodeData_shouldReturn_startSession() =
        mainCoroutineRule.runBlockingTest {
            val qrCodeData = QRCodeData(
                location_id = "ButterKnifeLib-1234",
                location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                        "Koramangala 1A Block, Bangalore",
                price_per_min = 5.50f,
                sessionStartTime = Calendar.getInstance().timeInMillis
            )
            fakeLocalDataSource.insertQRCodeData(qrCodeData)

            val list = dataRepository.getOnGoingSessionQRCodeData().toList()

            Assert.assertEquals(
                list, listOf(
                    ResultState.StartSession(fakeLocalDataSource.getQRCodeData()!!)
                )
            )
        }

    @Test
    fun startOrStopSession_shouldStartSession() = mainCoroutineRule.runBlockingTest {
        val startTime = 1628349527000L
        val endTime = 1628349661000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        val list = dataRepository.startOrStopSession(qrCodeData, endTime).toList()
        Assert.assertEquals(
            list, listOf(
                ResultState.StartSession(fakeLocalDataSource.getQRCodeData()!!)
            )
        )
    }

    @Test
    fun startOrStopSession_shouldEndSession_shouldGetSuccessServerResponse() =
        mainCoroutineRule.runBlockingTest {
            val startTime = 1628349527000L
            val endTime = 1628349661000L
            val qrCodeData = QRCodeData(
                location_id = "ButterKnifeLib-1234",
                location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                        "Koramangala 1A Block, Bangalore",
                price_per_min = 5.50f,
                sessionStartTime = startTime
            )
            fakeLocalDataSource.insertQRCodeData(qrCodeData)
            val list = dataRepository.startOrStopSession(qrCodeData, endTime).toList()

            Assert.assertEquals(
                list, listOf(
                    ResultState.Success(FakeResponseUtility.getSuccessResponse()),
                    ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button.")
                )
            )
            Assert.assertNull(fakeLocalDataSource.getQRCodeData())
        }

    @Test
    fun startOrStopSession_shouldEndSession_shouldGetErrorServerResponse() =
        mainCoroutineRule.runBlockingTest {
            val startTime = 1628349527000L
            val endTime = 1628349661000L
            val qrCodeData = QRCodeData(
                location_id = "ButterKnifeLib-1234",
                location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                        "Koramangala 1A Block, Bangalore",
                price_per_min = 5.50f,
                sessionStartTime = startTime
            )
            fakeLocalDataSource.insertQRCodeData(qrCodeData)
            (fakeRemoteDataSource as FakeRemoteDataSource).setStatus(FakeRemoteDataSource.Data.SHOULD_RETURN_ERROR)
            val list = dataRepository.startOrStopSession(qrCodeData, endTime).toList()

            Assert.assertEquals(
                list, listOf(
                    ResultState.Failure(FakeResponseUtility.getResponseWithError()),
                )
            )
            Assert.assertEquals(qrCodeData, fakeLocalDataSource.getQRCodeData())
        }
}