package com.example.callrecording.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CallLog
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.callrecording.MyApplication
import com.example.callrecording.activity.HomeActivity
import com.example.callrecording.activity.SplashScreen

import com.example.callrecording.database.tables.PhoneCall
import com.example.callrecording.recording.AnnouncementPlayer
import com.example.callrecording.service.BackgroundService
import com.example.callrecording.service.ForegroundService
import com.example.callrecording.service.RecordingService
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.PrefrenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class TelephonyBroadReceiver : BroadcastReceiver() {

    companion object {
        const val OUT_GOING_CALL_CONNECTED = "com.example.OUTGOING_CALL_CONNECTED"
    }

    override fun onReceive(context: Context, intent: Intent) {

        AppUtils.writeLogs("intent == ${intent.action}")


        when (intent.action ) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
            -> {

                ForegroundService.startBackgroundService(context)

                val i = Intent(context, SplashScreen::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }


        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        when (telephonyManager.callState) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {

                startCallRecording(context)


            }
            TelephonyManager.CALL_STATE_RINGING -> {
                MyApplication.isIncomingCall = true
            }

            TelephonyManager.CALL_STATE_IDLE -> {
                stopCallRecording(context)
            }
        }
    }


    private fun startCallRecording(context: Context) {
        if (!MyApplication.isCallStartRecord) {

            val accessibilityManager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

            if (!accessibilityManager.isEnabled) {
                AppUtils.writeLogs("Accessbility service turns off")

            }


            MyApplication.isCallStartRecord = true
            AppUtils.showToastMessage(context, "Call recording starting")
            val intent = Intent(context, RecordingService::class.java)
            intent.apply {
                action =
                    if (MyApplication.isIncomingCall)
                        RecordingService.ACTION_INCOMING_START_RECORDING
                    else
                        RecordingService.ACTION_OUTGOING_START_RECORDING
            }
            context.startService(intent)
            //Play announcement file
            if (MyApplication.isIncomingCall) {
                AnnouncementPlayer(context).playAnnouncement(true)
            }
        }

    }

    private fun stopCallRecording(context: Context) {
        MyApplication.isIncomingCall = false
        if (MyApplication.isCallStartRecord) {
            MyApplication.isCallStartRecord = false
            try {
                val intent = Intent(context, RecordingService::class.java).apply {
                    action = RecordingService.ACTION_STOP_RECORDING
                }
                context.startService(intent)
                if (PrefrenceUtils.getServiceStatus(context)) {
                    getLastCallDetail(context)
                } else {
                    AppUtils.writeLogs("Service Disabled to send data")
                }
            } catch (e: Exception) {
                AppUtils.showToastMessage(context, "Unable to save calls due to recording")
                AppUtils.writeLogs(e.toString())
            }
        }
    }


    @SuppressLint("Range")
    private fun getLastCallDetail(context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)

            var phoneCallTableEntry = PhoneCall()

            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION
            )

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cursor = context.contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        val id = it.getLong(it.getColumnIndex(CallLog.Calls._ID))
                        val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                        val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                        val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                        val duration = it.getInt(it.getColumnIndex(CallLog.Calls.DURATION))

                        phoneCallTableEntry = PhoneCall(
                            id = id,
                            number = number,
                            type =
                            if (type == 1)
                                "Incoming Call"
                            else
                                "Outgoing Call",
                            date = AppUtils.convertLongToDate(date),
                            time = AppUtils.convertLongToTime(date),
                            duration = "$duration",
                            upload = false,
                            filePath = MyApplication.recordFilePath,
                            fileSize = File(MyApplication.recordFilePath).length(),
                            driveLink = ""
                        )
                    }
                }
                runBackgroundUpload(context, phoneCallTableEntry)
            }
        }
    }


    private fun runBackgroundUpload(context: Context, callData: PhoneCall) {

        val durations = callData.duration!!.toInt()
        if (durations > 0) {
            AppUtils.writeLogs("Worker server intent send")
            val hours = durations / 3600
            val minutes = (durations % 3600) / 60
            val seconds = durations % 60
            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            callData.duration = timeString
            BackgroundService.phoneCall = callData
            val myTag = "${BackgroundService.phoneCall.id}"
            val myWorkRequest = OneTimeWorkRequestBuilder<BackgroundService>()
                .addTag(myTag)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(myTag, ExistingWorkPolicy.REPLACE, myWorkRequest)
        } else {
            AppUtils.writeLogs("Call not send to database becuase its 0 sec")
        }
    }
}
