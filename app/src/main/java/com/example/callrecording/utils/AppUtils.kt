package com.example.callrecording.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.arnab.storage.AppFileManager
import com.arnab.storage.FileLocationCategory
import com.example.callrecording.BuildConfig
import com.example.callrecording.MyAccessibilityService
import com.example.callrecording.service.ForegroundService
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {
    const val folderName = "CloseCall"
    const val defaultPreRecord = "announcement.mp3"

    fun createBaseFolder()
    {
        val appFileManager = AppFileManager(BuildConfig.APPLICATION_ID)
        appFileManager.createFolder(
            folderName = folderName,
            path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).absolutePath
        )
    }

    fun createRecordingFile(context:Context, callback: (File)->Unit){
        val appFileManager = AppFileManager(BuildConfig.APPLICATION_ID)

        createBaseFolder()

      // val filename = getCurrentDateTime() +"$$"+ PrefrenceUtils.getDeviceName(context)
        val filename = "test"
        val file = appFileManager.createFile(
            context = context,
         fileLocationCategory = FileLocationCategory.DOWNLOADS_DIRECTORY,
          fileName = "/$folderName/$filename",
       //  fileLocationCategory = FileLocationCategory.DATA_DIRECTORY,
         //fileName = "/$filename",
         fileExtension = "mp3"
        )

        Handler().postDelayed({
            callback(file)
        },2000)

    }

    fun getCurrentDateTime(): String {
        val now = Date(currentTimeToLong())
        val timeFormatter =SimpleDateFormat("HHmmss")
        val dateFormatter = SimpleDateFormat("ddMMyy")
        val timeString = timeFormatter.format(now)
        val dateString = dateFormatter.format(now)
        return "$timeString-$dateString"
    }

    fun showSnackMessage(msg: String, view: View) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    fun writeLogs(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Call App", msg)
        }
    }
    fun getMimeType(file: File): String? =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)



    fun showToastMessage(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }

    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    fun getCurrentSystemTimeDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
        return format.format(date)
    }


    private fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return df.parse(date).time
    }


     fun requestAccessibilityPermission(context:Context,settingsEnableNow:()->Unit) {
        //requestAccessibility(true)
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            requestAccessibility(context,true,settingsEnableNow)
        } else {

            accessibilityManager.addAccessibilityStateChangeListener(object : AccessibilityManager.AccessibilityStateChangeListener {
                override fun onAccessibilityStateChanged(enabled: Boolean) {
                    requestAccessibility(context,enabled,settingsEnableNow)
                    accessibilityManager.removeAccessibilityStateChangeListener(this)
                }
            })
            requestAccessibility(context,false,settingsEnableNow)
        }
    }

    private fun requestAccessibility(context:Context,accessibilityEnabled: Boolean,settingsEnableNow:()->Unit) {
        if (accessibilityEnabled) {

            settingsEnableNow()
        } else {
            // Accessibility is still not enabled, show the accessibility settings

            var intent = Intent("com.samsung.accessibility.installed_service")
            if (intent.resolveActivity(context.packageManager) == null) {
                intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            }
            val extraFragmentArgKey = ":settings:fragment_args_key"
            val extraShowFragmentArguments = ":settings:show_fragment_args"
            val bundle = Bundle()
            val showArgs: String = "${context.packageName}/${MyAccessibilityService::class.java.name}"
            bundle.putString(extraFragmentArgKey, showArgs)
            intent.putExtra(extraFragmentArgKey, showArgs)
            intent.putExtra(extraShowFragmentArguments, bundle)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }


        }
    }
     fun enableService(context: Context) {
        val packageManager: PackageManager = context.packageManager
        val componentName = ComponentName(context, ForegroundService::class.java)
        val settingCode = PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        packageManager.setComponentEnabledSetting(
            componentName,
            settingCode,
            PackageManager.DONT_KILL_APP
        )
    }


}