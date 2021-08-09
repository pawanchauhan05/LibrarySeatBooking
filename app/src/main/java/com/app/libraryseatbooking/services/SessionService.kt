package com.app.libraryseatbooking.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.app.libraryseatbooking.R
import com.app.libraryseatbooking.data.source.IDataRepository
import com.app.libraryseatbooking.ui.scannerDetails.ScannerDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random


private const val TAG = "SessionService"
private const val CHANNEL_ID = "SessionServiceChannel"
private const val NOTIFICATION_ID = 23432

@AndroidEntryPoint
class SessionService() : Service() {

    companion object {
        fun getIntent(context: Context) : Intent {
            return Intent(context, SessionService::class.java)
        }
    }

    @Inject
    lateinit var dataRepository: IDataRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val bag = CompositeDisposable()

    lateinit var notification: Notification

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent!!.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, ScannerDetailsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.library_session_started))
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_baseline_attribution_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)


        return START_NOT_STICKY
    }

    private fun updateNotification(contentText: String) {
        val notificationIntent = Intent(this, ScannerDetailsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.library_session_started))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_baseline_attribution_24)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Session Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
        job.cancel()
    }

    override fun onCreate() {
        super.onCreate()

        Observable
            .interval(30, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                scope.launch {
                    updateNotification(getString(R.string.time_min_ago, dataRepository.getOnGoingSessionTimeInMinutes(Calendar.getInstance().timeInMillis)))
                }
            }, {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }).run {
                bag.add(this)
            }
    }
}