package com.example.callrecording.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.callrecording.R
import com.example.callrecording.utils.AppUtils

class ForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 2121
        fun startBackgroundService(context: Context)
        {
            val intent = Intent(context, ForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
            AppUtils.enableService(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize any resources your service needs here
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onDestroy() {
        super.onDestroy()
       // AppUtils.writeLogs("Service onDestroy")
       // ForegroundService.startBackgroundService(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
       // AppUtils.writeLogs("Service onUnbind")
       // ForegroundService.startBackgroundService(this)

        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
      //  AppUtils.writeLogs("Service onTaskRemoved")
      //  ForegroundService.startBackgroundService(this)
    }


    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "Call Recording Service,")
            } else {
                  ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .setSound(null)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}