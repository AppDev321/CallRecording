package com.example.callrecording

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }

    override fun onStart() {

        super.onStart()
        Log.e(TAG, "Activity start")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val runtimePermissionsArrayList = ArrayList<String>()
            runtimePermissionsArrayList.add(Manifest.permission.INTERNET)
            runtimePermissionsArrayList.add(Manifest.permission.READ_PHONE_STATE)
            runtimePermissionsArrayList.add(Manifest.permission.CALL_PHONE)
            runtimePermissionsArrayList.add(Manifest.permission.RECORD_AUDIO)
            runtimePermissionsArrayList.add(Manifest.permission.READ_CONTACTS)
            runtimePermissionsArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            runtimePermissionsArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            runtimePermissionsArrayList.add(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                runtimePermissionsArrayList.add(Manifest.permission.FOREGROUND_SERVICE)
            }
            if (!runtimePermissionsArrayList.isEmpty()) {
                val requestRuntimePermissionsArrayList = ArrayList<String>()
                for (requestRuntimePermission in runtimePermissionsArrayList) {
                    if (checkSelfPermission(requestRuntimePermission) != PackageManager.PERMISSION_GRANTED) {
                        requestRuntimePermissionsArrayList.add(requestRuntimePermission)
                    }
                }
                if (!requestRuntimePermissionsArrayList.isEmpty()) {
                    requestPermissions(requestRuntimePermissionsArrayList.toTypedArray(), 1)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_GRANTED) {
                var powerManager: PowerManager? = null
                try {
                    powerManager = getSystemService(POWER_SERVICE) as PowerManager
                } catch (e: Exception) {

                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
                if (powerManager != null) {
                    if (powerManager.isIgnoringBatteryOptimizations(packageName)) {

                        Log.e(
                            TAG,
                            "2. Request ignore battery optimizations (\"1.\" alternative; with package URI) - Entire application: Enabled"
                        )

                    } else {
                        Log.e(
                            TAG,
                            "1. Request ignore battery optimizations (\"1.\" alternative; with package URI) - Entire application: Enabled"
                        )


                        /*  val intent: Intent =
                                RequestIgnoreBatteryOptimizationsUtil.getRequestIgnoreBatteryOptimizationsIntent(
                                    this
                                )
                            if (intent != null) {
                                startActivityForResult(intent, 2)
                            }*/
                    }
                }
            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults.size == permissions.size) {
                var allGranted = true
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                    }
                }
                if (allGranted) {

                    Log.e(TAG, "All requested permissions are granted")
                } else {
                    Log.e(TAG, "Not all requested permissions are granted")


                    requestAccessibilityPermission()

                    /*   val repo=  AudioRecorder(this)

                         Log.e("recording","starttedddd")
                         val sampleDir = File(
                             Environment.getExternalStoragePublicDirectory(
                                 Environment.DIRECTORY_DOWNLOADS
                             ), "/AlphaCall/"
                         )
                         Log.e("Java Service", "" + sampleDir.exists())
                         if (!sampleDir.exists()) {
                             sampleDir.mkdirs()
                             Log.e("Java Service", "Create Directory")
                         }
                         var filepath = sampleDir.path

                         filepath += "/test.mp3"
                         repo.start(  File(filepath))
                         GlobalScope.launch {
                             delay(6000)
                             Log.e("recording","Stooppedddd")
                             repo.stop()
                         }*/
                }
            }
        }
    }


    private fun requestAccessibilityPermission() {
        var intent = Intent("com.samsung.accessibility.installed_service")
        if (intent.resolveActivity(packageManager) == null) {
            intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }
        val extraFragmentArgKey = ":settings:fragment_args_key"
        val extraShowFragmentArguments = ":settings:show_fragment_args"
        val bundle = Bundle()
        val showArgs: String = "${packageName}/${MyAccessibilityService::class.java.name}"
        bundle.putString(extraFragmentArgKey, showArgs)
        intent.putExtra(extraFragmentArgKey, showArgs)
        intent.putExtra(extraShowFragmentArguments, bundle)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
    }
}