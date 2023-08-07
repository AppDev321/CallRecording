package com.example.callrecording.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.callrecording.MyApplication
import com.example.callrecording.database.tables.PhoneCall
import com.example.callrecording.databinding.ActivitySplashBinding
import com.example.callrecording.service.ForegroundService
import com.gm.permission.GmPermissionManager
import com.gm.permission.PermissionListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {

    private lateinit var permissionManager: GmPermissionManager
    private lateinit var binding: ActivitySplashBinding
    private val REQUEST_CODE_IGNORE_BATTERY_OPTIMIZATION =222

    private val permissionList = arrayOf(

        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS

//
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        checkPermissions()

      //  insertDummyCall()


        ForegroundService.startBackgroundService(this)

    }
    private fun insertDummyCall()
    {
        val c1=   PhoneCall(5221,"","03336610039","Outgoing Call","2023-04-29","13:59","0 min","https://drive.google.com/file/d/1JprYgZAh_Zb6z7u_YXZLEwBkMBottWot/view?usp=drivesdk","/storage/emulated/0/Download/Alpha Call/sample.jpg",false,62811)
        val c2=    PhoneCall(5222,"","03336610039","Outgoing Call","2023-04-29","13:59","0 min","https://drive.google.com/file/d/1JprYgZAh_Zb6z7u_YXZLEwBkMBottWot/view?usp=drivesdk","/storage/emulated/0/Download/Alpha Call/sample.jpg",false,62811)
        val c3=   PhoneCall(5220,"","03336610039","Outgoing Call","2023-04-29","13:59","0 min","https://drive.google.com/file/d/1JprYgZAh_Zb6z7u_YXZLEwBkMBottWot/view?usp=drivesdk","/storage/emulated/0/Download/Alpha Call/sample.jpg",false,62811)
        val c4=    PhoneCall(4221,"","0335","Incoming Call","2023-04-29","13:59","0 min","https://drive.google.com/file/d/1JprYgZAh_Zb6z7u_YXZLEwBkMBottWot/view?usp=drivesdk","/storage/emulated/0/Download/Alpha Call/sample.jpg",false,62811)
        val c5=     PhoneCall(4220,"","0335","Incoming Call","2023-04-29","13:59","0 min","https://drive.google.com/file/d/1JprYgZAh_Zb6z7u_YXZLEwBkMBottWot/view?usp=drivesdk","/storage/emulated/0/Download/Alpha Call/sample.jpg",false,62811)
        lifecycleScope.launch {

            MyApplication.repository.insert(c1)
            MyApplication.repository.insert(c2)
            MyApplication.repository.insert(c3)
            MyApplication.repository.insert(c4)
            MyApplication.repository.insert(c5)
        }

    }

    private fun checkPermissions() {
        permissionManager = GmPermissionManager.builder()
            .with(this)
            .requestCode(101)
            .neededPermissions(
                permissionList
            )
            .setPermissionListner(object : PermissionListener {
                override fun onPermissionsGranted(requestCode: Int, acceptedPermission: String) {
                    lifecycleScope.launch {
                        delay(2000)
                        startActivity(Intent(this@SplashScreen,HomeActivity::class.java))
                        finish()
                    }

                }

                override fun showGrantDialog(grantPermissionTo: String): Boolean {

                    Snackbar.make(
                        binding.root,
                        permissionManager.getPermissionMessageDialog(grantPermissionTo).toString(),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Grant") { permissionManager.requestPermissions() }
                        .show()
                    return super.showGrantDialog(grantPermissionTo)
                }

                override fun showRationalDialog(
                    requestCode: Int,
                    deniedPermission: String
                ): Boolean {
                    Snackbar.make(
                        binding.root,
                        permissionManager.getPermissionMessageDialog(deniedPermission),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Setting") {
                            GmPermissionManager.gotoSettings(this@SplashScreen)
                        }
                        .show()
                    return super.showRationalDialog(requestCode, deniedPermission)
                }

            })
            .build()

        permissionManager.requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }








}
