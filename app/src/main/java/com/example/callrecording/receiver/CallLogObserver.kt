package com.example.callrecording.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.CallLog
import android.util.Log
// Create an instance of the CallLogObserver class
/* val callLogObserver = CallLogObserver(context)
  callLogObserver.startGettingCallLogs()*/
class CallLogObserver(private val context: Context) : ContentObserver(null) {
    private var callStartTime = 0L
    private var isCallInProgress = false

    private val callLogProjection = arrayOf(
        CallLog.Calls._ID,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.CACHED_NUMBER_TYPE,
        CallLog.Calls.CACHED_NUMBER_LABEL,
        CallLog.Calls.IS_READ
    )

    private val callLogSelection = "${CallLog.Calls.TYPE} = ${CallLog.Calls.OUTGOING_TYPE}"

    private val callLogSortOrder = "${CallLog.Calls.DATE} DESC"

    private val callLogHandler = Handler(Looper.getMainLooper())

    private val callLogRunnable = object : Runnable {
        @SuppressLint("Range")
        override fun run() {
            if (isCallInProgress) {
                // Get the call log
                val cursor = context.contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    callLogProjection,
                    callLogSelection,
                    null,
                    callLogSortOrder
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        // Get the call log details
                        val id = it.getLong(it.getColumnIndex(CallLog.Calls._ID))
                        val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                        val type = it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))
                        val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                        val duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))
                        val cachedName = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME))
                        val cachedNumberType = it.getInt(it.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE))
                        val cachedNumberLabel = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL))
                        val isRead = it.getInt(it.getColumnIndex(CallLog.Calls.IS_READ))

                        // Do something with the call log details
                        Log.d("CallLogObserver", "Call log - ID: $id, Number: $number, Type: $type, Date: $date, Duration: $duration, CachedName: $cachedName, CachedNumberType: $cachedNumberType, CachedNumberLabel: $cachedNumberLabel, IsRead: $isRead")
                    }
                }

                // Schedule the next call log check
                callLogHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        if (!isCallInProgress) {
            return
        }

        // The call log has been updated
        // Stop getting call logs and do something else
        stopGettingCallLogs()
    }

    fun startGettingCallLogs() {
        if (!isCallInProgress) {
            isCallInProgress = true
            callStartTime = SystemClock.elapsedRealtime()

            // Start getting call logs
            callLogHandler.post(callLogRunnable)
        }
    }

    fun stopGettingCallLogs() {
        if (isCallInProgress) {
            isCallInProgress = false

            // Stop getting call logs
            callLogHandler.removeCallbacks(callLogRunnable)
        }
    }
}
