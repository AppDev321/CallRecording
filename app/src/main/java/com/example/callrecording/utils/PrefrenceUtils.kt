package com.example.callrecording.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.callrecording.R

object PrefrenceUtils {

    fun getDeviceName(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.device_name), "") as String
    }
    fun setDeviceName(context: Context,deviceName: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.device_name), deviceName)
        editor.apply()
    }


    fun getCompanyName(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.company_name), "") as String
    }
    fun setCompanyName(context: Context,deviceName: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.company_name), deviceName)
        editor.apply()
    }


    fun getServiceStatus(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(context.getString(R.string.record_service), true)
    }
    fun setServiceStatus(context: Context,status: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.record_service), status)
        editor.apply()
    }


    fun getAudioSource(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.audio_source), "0") as String
    }

    fun setAnnouncementStatus(context: Context,status:Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(context.getString(R.string.announcement_status), status)
        editor.apply()
    }

    fun getAnnouncementStatus(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(context.getString(R.string.announcement_status), true)
    }

    fun getIncomingAnnouncementSeconds(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.incoming_delay), "0") as String
    }
    fun getOutgoingAnnouncementSeconds(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.outgoing_delay),  "0") as String
    }

}