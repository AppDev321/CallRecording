package com.example.callrecording.service

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.callrecording.MyApplication
import com.example.callrecording.R
import com.example.callrecording.model.GeneralResponse
import com.example.callrecording.recording.AudioRecorder
import com.example.callrecording.retrofit.ApiClient
import com.example.callrecording.retrofit.ApiInterface
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.PrefrenceUtils
import kotlinx.coroutines.*
import java.io.File
import kotlin.time.Duration.Companion.seconds


class RecordingService : Service() {

    private var isRecording = false
    private lateinit var notificationBuilder: NotificationCompat.Builder
    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_INCOMING_START_RECORDING -> {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(true, "Recording has been started")
                )
                startRecording()

            }

            ACTION_OUTGOING_START_RECORDING -> {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(true, "Recording has been started")
                )
                startRecording()

            }

            ACTION_STOP_RECORDING -> {
                stopRecording()
                stopSelf()
            }
            ACTION_PAUSE_RECORDING -> {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(false, "Recording has been paused")
                )
                MyApplication.voiceRecord.pause()
            }
            ACTION_RESUME_RECORDING -> {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(true, "Call is now Recorded again")
                )
                MyApplication.voiceRecord.resume()
            }
        }
        return START_STICKY
    }

    @SuppressLint("SuspiciousIndentation")
    private fun createNotification(isPause: Boolean, message: String): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_channel_id", "My Channel")
            } else {
                ""
            }

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Call Recording")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)

        notificationBuilder.apply {
            if (isPause)
                this.addAction(
                    R.drawable.baseline_pause_circle_24,
                    "Pause",
                    createActionIntent(isPause)
                )
            else
                this.addAction(
                    R.drawable.baseline_play_circle_24,
                    "Resume",
                    createActionIntent(isPause)
                )

        }
        //  .setContentIntent(createPendingIntent())

        return notificationBuilder.build()
    }

    private fun createActionIntent(isPause: Boolean): PendingIntent {
        val intent = Intent(this, RecordingService::class.java).apply {
            action = if (isPause)
                ACTION_PAUSE_RECORDING
            else
                ACTION_RESUME_RECORDING
        }
        return PendingIntent.getService(this, 0, intent, FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()

    }



    private fun startRecording() {
        AppUtils.createRecordingFile(this) {
            MyApplication.recordFilePath = it.absolutePath
            AppUtils.writeLogs("start recording")
            try {
                MyApplication.voiceRecord = AudioRecorder(this)
                MyApplication.voiceRecord.start(it)

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    AppUtils.showToastMessage(this, "Unable to start recording")
                    AppUtils.writeLogs(e.toString())
                }
            }
        }
        isRecording = true

    }

    private fun stopRecording() {
        if (isRecording) {
            MyApplication.voiceRecord.stop()
            isRecording = false
        }

    }

    private fun sendErrorLogs(msg: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.addError(errorMessage = msg)
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }


            } catch (e: Exception) {

            }
        }

    }


    companion object {
        const val ACTION_OUTGOING_START_RECORDING = "com.example.app.OUTGOING_START_RECORDING"
        const val ACTION_INCOMING_START_RECORDING = "com.example.app.INCOMING_START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.app.STOP_RECORDING"
        const val ACTION_PAUSE_RECORDING = "com.example.app.PAUSE_RECORDING"
        const val ACTION_RESUME_RECORDING = "com.example.app.RESUME_RECORDING"
        const val NOTIFICATION_ID = 10231
    }
}
