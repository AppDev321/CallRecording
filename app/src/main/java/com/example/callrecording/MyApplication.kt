package com.example.callrecording

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.net.sip.SipManager
import android.net.sip.SipProfile
import android.net.sip.SipRegistrationListener
import com.example.callrecording.database.AppDatabase
import com.example.callrecording.database.repository.PhoneCallRepo
import com.example.callrecording.recording.AudioRecorder
import com.example.callrecording.utils.AppUtils

class MyApplication : Application() {
    companion object {
        const val preRecordingFilePath = "/storage/emulated/0/Download/${AppUtils.folderName}/${AppUtils.defaultPreRecord}"
        private lateinit var database: AppDatabase
        lateinit var repository: PhoneCallRepo
        var isCallStartRecord =false
        var recordFilePath = ""
        lateinit var voiceRecord: AudioRecorder
        var isIncomingCall = false
    }

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getDatabase(this)
        repository = PhoneCallRepo(database.phoneCallDao())


    }
}