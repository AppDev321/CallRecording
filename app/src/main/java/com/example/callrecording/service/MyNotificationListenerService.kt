package com.example.callrecording.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.callrecording.MyApplication
import com.example.callrecording.receiver.TelephonyBroadReceiver
import com.example.callrecording.recording.AnnouncementPlayer
import com.example.callrecording.utils.AppUtils

class MyNotificationListenerService : NotificationListenerService() {

    companion object {
        const val CHANNEL_ID_PHONE_OUTGOING_CALL_13 = "phone_ongoing_call"
        const val CHANNEL_ID_PHONE_INCOMING_CALL_13 = "phone_incoming_call"
        const val CHANNEL_ID_PHONE_MISSED_CALL_13 = "phone_missed_call"


        var CHANNEL_ID_PHONE_OUTGOING_CALL_8 = "ongoingCall"
        var CHANNEL_ID_PHONE_INCOMING_CALL_8 = "incomingCall"
        var CHANNEL_ID_PHONE_OUTGOING_CALL_2_8 = "ongoingCall2"
        var CHANNEL_ID_PHONE_MISSED_CALL_8 = "missedCall"


        const val CATEGORY_CALL = "call"
        const val DIALING_STATUS = "Dialling"
    }

    private var lastDialingStatus: String? = null

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val notification = sbn?.notification
        val packageName = sbn?.packageName
        val channelID = notification?.channelId
        val category = notification?.category
        val text = notification?.extras?.getString(Notification.EXTRA_TEXT)
     //   Log.e("MyNotificationListener", "${channelID} == ${notification}")

        if ((channelID == CHANNEL_ID_PHONE_OUTGOING_CALL_13 || channelID == CHANNEL_ID_PHONE_OUTGOING_CALL_8 || channelID == CHANNEL_ID_PHONE_OUTGOING_CALL_2_8) && category == CATEGORY_CALL) {
            if (text?.contains(DIALING_STATUS) == true) {
                lastDialingStatus = DIALING_STATUS
                Log.e("MyNotificationListener", "Outgoing call is ringing")
            } else if (lastDialingStatus == DIALING_STATUS) {
                lastDialingStatus = null
                Log.e("MyNotificationListener", "Call Connected now play announcement file")
                if (MyApplication.isCallStartRecord) {
                    AnnouncementPlayer(this).playAnnouncement(false)
                }

            }
        }
    }
}


