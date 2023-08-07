package com.example.callrecording.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import java.text.DecimalFormat

class CallLogsHelper(val context: Context) {

    fun findNameByNumber(phoneNumber: String): String? {
        val cr: ContentResolver = context.getContentResolver()
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor = cr.query(
            uri, arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME
            ), null, arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME
            ), null
        )
            ?: return null
        var contactName: String? = null
        if (cursor.moveToFirst()) {
            contactName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        return contactName ?: phoneNumber
    }

    fun getCoolDuration(sum: Float): String? {
        var duration = ""
        val result: String
        if (sum >= 0 && sum < 3600) {
            result = (sum / 60).toString()
            val decimal = result.substring(0, result.lastIndexOf("."))
            val point = "0" + result.substring(result.lastIndexOf("."))
            val minutes = decimal.toInt()
            val seconds = point.toFloat() * 60
            val formatter = DecimalFormat("#")
            duration = minutes.toString() + " min " + formatter.format(seconds.toDouble()) + " secs"
        } else if (sum >= 3600) {
            result = (sum / 3600).toString()
            val decimal = result.substring(0, result.lastIndexOf("."))
            val point = "0" + result.substring(result.lastIndexOf("."))
            val hours = decimal.toInt()
            val minutes = point.toFloat() * 60
            val formatter = DecimalFormat("#")
            duration = hours.toString() + " hrs " + formatter.format(minutes.toDouble()) + " min"
        }
        return duration
    }
}