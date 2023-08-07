package com.example.callrecording.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import com.example.callrecording.model.CallLogData

import java.util.*

class CallLogsUtils(val context: Context) {

    val callLogsHelper: CallLogsHelper
    init {
        callLogsHelper = CallLogsHelper(context)
    }

    companion object {
        val INCOMING_TYPE = CallLog.Calls.INCOMING_TYPE
        val OUTGOING_TYPE = CallLog.Calls.OUTGOING_TYPE
        val MISSED_TYPE = CallLog.Calls.MISSED_TYPE
        val INCOMING_CALLS = 672
        val OUTGOING_CALLS = 609
        val MISSED_CALLS = 874
        val ALL_CALLS = 814
    }

    @SuppressLint("MissingPermission")
    private fun getCallLogs(callType: Int): ArrayList<CallLogData> {
        var logs = ArrayList<CallLogData>()
        var callLogType:String? = null
        when(callType) {
            INCOMING_CALLS -> callLogType = CallLog.Calls.TYPE + " = " + INCOMING_TYPE
            OUTGOING_CALLS -> callLogType = CallLog.Calls.TYPE + " = " + OUTGOING_TYPE
            MISSED_CALLS -> callLogType = CallLog.Calls.TYPE + " = " + MISSED_TYPE
            ALL_CALLS -> callLogType = null
            else -> callLogType = null
        }

        val cursor: Cursor? = context.getContentResolver().query(
            CallLog.Calls.CONTENT_URI,
            null as Array<String?>?,
            callLogType,
            null as Array<String?>?,
            null as String?
        )
        val number = cursor?.getColumnIndex("number")
        val type = cursor?.getColumnIndex("type")
        val date = cursor?.getColumnIndex("date")
        val duration = cursor?.getColumnIndex("duration")

        while (cursor!!.moveToNext()) {
            val log = CallLogData()
            log.name = callLogsHelper.findNameByNumber(cursor.getString(number!!))
            log.number = cursor.getString(number)
            log.type = cursor.getInt(type!!)
            log.duration = callLogsHelper.getCoolDuration(cursor.getInt(duration!!).toFloat())
            log.date = cursor.getLong(date!!)
            logs.add(log)
        }
        return logs
    }

    fun getAllCallLogs(): ArrayList<CallLogData> {
        return getCallLogs(ALL_CALLS)
    }

    fun getAllCallLogsCount(): Int {
        return getAllCallLogs().size
    }

    fun getIncomingCallLogs(): ArrayList<CallLogData> {
        return getCallLogs(INCOMING_CALLS)
    }

    fun getIncomingCallLogsCount(): Int {
        return getIncomingCallLogs().size
    }

    fun getOutgoingCallLogs(): ArrayList<CallLogData> {
        return getCallLogs(OUTGOING_CALLS)
    }

    fun getOutgoingCallLogsCount(): Int {
        return getOutgoingCallLogs().size
    }

    fun getMissedCallLogs(): ArrayList<CallLogData> {
        return getCallLogs(MISSED_CALLS)
    }

    fun getMissedCallLogsCount(): Int {
        return getMissedCallLogs().size
    }
}