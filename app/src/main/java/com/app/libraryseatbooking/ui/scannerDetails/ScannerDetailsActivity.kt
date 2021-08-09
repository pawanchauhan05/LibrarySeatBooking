package com.app.libraryseatbooking.ui.scannerDetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.libraryseatbooking.R
import com.app.libraryseatbooking.data.source.IDataRepository
import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import com.app.libraryseatbooking.services.SessionService
import com.app.libraryseatbooking.ui.scanner.ScannerActivity
import com.app.libraryseatbooking.utilities.Config
import com.app.libraryseatbooking.utilities.Utility
import com.app.libraryseatbooking.utilities.visibility
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_scanner_details.*
import kotlinx.coroutines.CoroutineDispatcher
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ScannerDetailsActivity"


@AndroidEntryPoint
class ScannerDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private val bag: CompositeDisposable = CompositeDisposable()

    private lateinit var qrCodeData: QRCodeData

    @Inject
    lateinit var dataRepository: IDataRepository

    @Inject
    lateinit var dispatcher: CoroutineDispatcher

    private val scannerDetailsViewModel by viewModels<ScannerDetailsViewModel> {
        ScannerDetailsModelFactory(dataRepository, dispatcher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_details)

        initListeners()
        initObservers()
        initTimer()
        scannerDetailsViewModel.getOnGoingSession()
    }

    private fun initTimer() {
        Observable
            .interval(60, TimeUnit.SECONDS)
            .subscribe({
                if (this::qrCodeData.isInitialized) {
                    textViewTimer.text = getString(
                        R.string.session_started_min_ago, Utility.getMinBetweenDates(
                            endTime = Calendar.getInstance().timeInMillis,
                            startTime = qrCodeData.sessionStartTime
                        )
                    )
                }
            }, {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }).run {
                bag.add(this)
            }
    }

    private fun initListeners() {
        buttonScan.setOnClickListener(this)
        endSession.setOnClickListener(this)
    }

    private fun initObservers() {
        scannerDetailsViewModel.onGoingSession.observe(this, {
            when (it) {
                is ResultState.StartSession -> {
                    endSession.visibility(true)
                    buttonScan.visibility(false)
                    textViewWelcome.visibility(false)
                    textViewLocationId.visibility(true)
                    textViewLocationDetails.visibility(true)
                    textViewPrice.visibility(true)
                    textViewStartTime.visibility(true)
                    textViewTimer.visibility(true)
                    textViewTotalAmount.visibility(true)

                    qrCodeData = it.qrCodeData
                    val minAgo =
                        Utility.getMinBetweenDates(
                            endTime = Calendar.getInstance().timeInMillis,
                            startTime = qrCodeData.sessionStartTime
                        )

                    textViewLocationId.text =
                        getString(R.string.location_id, it.qrCodeData.location_id)
                    textViewLocationDetails.text =
                        getString(R.string.location_details, it.qrCodeData.location_detail)
                    textViewPrice.text =
                        getString(R.string.price_per_minute, it.qrCodeData.price_per_min)
                    textViewStartTime.text = getString(
                        R.string.start_time, Utility.getFormattedTime(
                            Config.TIME_FORMAT,
                            it.qrCodeData.sessionStartTime
                        )
                    )
                    textViewTimer.text = getString(R.string.session_started, minAgo)
                    val totalAmount = if (minAgo == 0L) {
                        it.qrCodeData.price_per_min
                    } else {
                        it.qrCodeData.price_per_min * minAgo
                    }
                    textViewTotalAmount.text = getString(R.string.total_amount, totalAmount)

                    val serviceIntent = Intent(
                        this,
                        SessionService::class.java
                    ).apply {
                        putExtra("inputExtra", getString(R.string.time_min_ago, minAgo))
                    }
                    ContextCompat.startForegroundService(this, serviceIntent)
                }

                is ResultState.NoSession -> {
                    textViewWelcome.apply {
                        text = it.message
                        visibility(true)
                    }

                    textViewLocationId.visibility(false)
                    textViewLocationDetails.visibility(false)
                    textViewPrice.visibility(false)
                    textViewStartTime.visibility(false)
                    textViewTimer.visibility(false)
                    textViewTotalAmount.visibility(false)
                    endSession.visibility(false)
                    buttonScan.visibility(true)

                    // to make sure service should not be running in no active session state
                    stopService(SessionService.getIntent(this))
                }

                is ResultState.Failure -> {
                    when (it.exception) {
                        is SocketTimeoutException -> {
                            Toast.makeText(
                                this,
                                getString(R.string.timeout_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UnknownHostException -> {
                            Toast.makeText(
                                this,
                                getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this,
                                it.exception.message ?: getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is ResultState.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.session_ended_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    stopService(SessionService.getIntent(this))
                }
            }
        })

        scannerDetailsViewModel.dataLoading.observe(this, { isVisible ->
            progressBar.visibility(isVisible)
        })
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.buttonScan -> {
                startScan()
            }

            R.id.endSession -> {
                if (this::qrCodeData.isInitialized) {
                    scannerDetailsViewModel.startOrStopSession(qrCodeData)
                }
            }
        }
    }

    private fun startScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(getString(R.string.scan_qr_code))
        integrator.captureActivity = ScannerActivity::class.java
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            result?.let {
                if (it.contents == null) {
                    Toast.makeText(this, getString(R.string.scanning_cancelled), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    var qrData = result.contents.replace("\\\"", "\"")
                    if (qrData.substring(0, 2) == "\"{") {
                        qrData = qrData.replaceRange(0, 2, "{")
                    }
                    if (qrData.substring(qrData.length - 2, qrData.length) == "}\"") {
                        qrData = qrData.replaceRange(qrData.length - 2, qrData.length, "}")
                    }

                    var qrCodeData = Gson().fromJson(qrData, QRCodeData::class.java)
                    qrCodeData.sessionStartTime = Calendar.getInstance().timeInMillis
                    scannerDetailsViewModel.startOrStopSession(qrCodeData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}