package com.app.libraryseatbooking.ui.scannerDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.libraryseatbooking.FakeResponseUtility
import com.app.libraryseatbooking.MainCoroutineRule
import com.app.libraryseatbooking.data.source.DataRepository
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import com.app.libraryseatbooking.data.source.remote.FakeRemoteDataSource
import com.app.libraryseatbooking.data.source.remote.IRemoteDataSource
import com.app.libraryseatbooking.getOrAwaitValue
import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@ExperimentalCoroutinesApi
class ScannerDetailsViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fakeLocalDataSource: ILocalDataSource

    @Inject
    lateinit var fakeRemoteDataSource: IRemoteDataSource

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataRepository: DataRepository
    private lateinit var scannerDetailsViewModel: ScannerDetailsViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        hiltRule.inject()
        dataRepository = DataRepository(fakeLocalDataSource, fakeRemoteDataSource, dispatcher)
        scannerDetailsViewModel = ScannerDetailsViewModel(dataRepository, dispatcher)
    }

    @Test
    fun getOnGoingSession_shouldReturnNoSession() = (dispatcher as TestCoroutineDispatcher).runBlockingTest {
        pauseDispatcher()
        scannerDetailsViewModel.getOnGoingSession()

        val data1 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data1 , true)

        resumeDispatcher()

        val data2 = scannerDetailsViewModel.onGoingSession.getOrAwaitValue()
        Assert.assertEquals(data2 , ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button."))

        val data3 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data3 , false)

    }

    @Test
    fun getOnGoingSession_shouldStartSession() = (dispatcher as TestCoroutineDispatcher).runBlockingTest {
        val startTime = 1628349527000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        fakeLocalDataSource.insertQRCodeData(qrCodeData)

        pauseDispatcher()
        scannerDetailsViewModel.getOnGoingSession()

        val data1 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data1 , true)

        resumeDispatcher()

        val data2 = scannerDetailsViewModel.onGoingSession.getOrAwaitValue()
        Assert.assertEquals(data2 , ResultState.StartSession(fakeLocalDataSource.getQRCodeData()!!))

        val data3 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data3 , false)
    }

    @Test
    fun startOrStopSession_shouldEndSession_shouldGetSuccessServerResponse() = (dispatcher as TestCoroutineDispatcher).runBlockingTest {
        val startTime = 1628349527000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        fakeLocalDataSource.insertQRCodeData(qrCodeData)

        pauseDispatcher()
        scannerDetailsViewModel.startOrStopSession(qrCodeData)

        val data1 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data1 , true)

        resumeDispatcher()

        val data2 = scannerDetailsViewModel.onGoingSession.getOrAwaitValue()
        Assert.assertEquals(data2 , ResultState.NoSession("You do not have any ongoing session.\nPlease click on SCAN NOW button."))

        val data3 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data3 , false)

    }

    @Test
    fun startOrStopSession_shouldEndSession_shouldGetErrorServerResponse() = (dispatcher as TestCoroutineDispatcher).runBlockingTest {
        val startTime = 1628349527000L
        val qrCodeData = QRCodeData(
            location_id = "ButterKnifeLib-1234",
            location_detail = "ButterKnife Lib, 80 Feet Rd,\n" +
                    "Koramangala 1A Block, Bangalore",
            price_per_min = 5.50f,
            sessionStartTime = startTime
        )
        fakeLocalDataSource.insertQRCodeData(qrCodeData)
        (fakeRemoteDataSource as FakeRemoteDataSource).setStatus(FakeRemoteDataSource.Data.SHOULD_RETURN_ERROR)

        pauseDispatcher()
        scannerDetailsViewModel.startOrStopSession(qrCodeData)

        val data1 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data1 , true)

        resumeDispatcher()

        val data2 = scannerDetailsViewModel.onGoingSession.getOrAwaitValue()
        Assert.assertEquals(data2 , ResultState.Failure(FakeResponseUtility.getResponseWithError()))

        val data3 = scannerDetailsViewModel.dataLoading.getOrAwaitValue()
        Assert.assertEquals(data3 , false)
    }

}