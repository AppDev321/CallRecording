package com.example.callrecording.activity


import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.callrecording.service.MyNotificationListenerService
import com.example.callrecording.R
import com.example.callrecording.databinding.ActivityHomeBinding
import com.example.callrecording.model.DeviceDetail
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.PrefrenceUtils
import com.example.callrecording.utils.ProgressDialog
import com.example.callrecording.viewmodel.AppViewModel
import com.example.callrecording.viewmodel.Result
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding

    private lateinit var nameAlertDialog: AlertDialog


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PrefrenceUtils.setAnnouncementStatus(this,false)

        ignoreBatteryOptimization()

        AppUtils.createBaseFolder()
       // notificationPermissionCheck()

        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            binding.containerAccessbility.visibility= View.GONE
        } else {
            binding.containerAccessbility.visibility= View.VISIBLE
            binding.btnAccessSetting.setOnClickListener {
                AppUtils.requestAccessibilityPermission(this@HomeActivity)
                {
                    binding.containerAccessbility.visibility = View.GONE
                }
            }
        }


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_incoming,
                R.id.navigation_outgoing

            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val dialog = ProgressDialog.progressDialog(this)


        val deviceName =PrefrenceUtils.getDeviceName(this)
        if (deviceName.isEmpty()) {
            nameAlertDialog = showDialogForName { name,companyName ->
              //  appViewModel.addDeviceOnSheet(name,companyName)
                PrefrenceUtils.setDeviceName(
                    this@HomeActivity,
                    name
                )
                nameAlertDialog.dismiss()
            }
        }

        lifecycleScope.launch {
            appViewModel.apiResult.collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        dialog.show()
                    }
                    is Result.Success -> {
                        dialog.dismiss()
                        val response = result.data
                        if (response.status == true) {
                            //Save name in pref
                        val data =     response.data as List<DeviceDetail>
                            val deviceName = data[0].deviceName
                            val companyName = data[0].companyName
                            PrefrenceUtils.setDeviceName(
                                this@HomeActivity,
                                deviceName.toString()
                            )
                          /*  PrefrenceUtils.setCompanyName(
                                this@HomeActivity,
                                companyName.toString()
                            )*/
                            nameAlertDialog.dismiss()
                        } else {
                            AppUtils.showSnackMessage(
                                response.message.toString(),
                                binding.root
                            )
                        }
                    }
                    is Result.Error -> {
                        dialog.dismiss()
                        val error = result.exception
                        AppUtils.showSnackMessage(error.toString(), binding.root)
                    }
                    else -> {}
                }
            }
        }


        //appViewModel.uploadCallRecord(this@HomeActivity, File("/storage/emulated/0/Download/Alpha Call/sample.jpg"))

    }

    private fun notificationPermissionCheck() {
        if (isPermissionRequired()) {
            requestNotificationPermission()
        } else {
           // startBackground()
        }
    }

    private fun isPermissionRequired(): Boolean {
        val cn = ComponentName(this, MyNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(this.contentResolver, "enabled_notification_listeners")
        val enabled = flat != null && flat.contains(cn.flattenToString())
        return !enabled
    }

    private fun requestNotificationPermission() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        startActivity(intent)
    }


    private fun showDialogForName(onResult: (String,String) -> Unit): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Name Required")
        builder.setCancelable(false)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val inputDevice = EditText(this)
        inputDevice.hint = "Enter Unique ID"
        inputDevice.inputType = InputType.TYPE_CLASS_NUMBER

        layout.addView(inputDevice)
        val inputCompany = EditText(this)
        inputCompany.hint = "Enter company name"
        inputCompany.inputType = InputType.TYPE_CLASS_TEXT
       // layout.addView(inputCompany)
        builder.setView(layout)


        builder.setPositiveButton("OK") { _, _ ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val deviceName = inputDevice.text.toString().trim()
            val companyName = inputCompany.text.toString().trim()
         //   if (deviceName.isEmpty() || deviceName.length < 5 || companyName.isEmpty() || companyName.length < 5 ) {

            if (deviceName.isEmpty() || deviceName.length < 5  ) {

                AppUtils.showSnackMessage("Please enter name above 5 character", binding.root)
            } else {
                onResult(deviceName,companyName)
            }
        }



        return alertDialog
    }


    private fun ignoreBatteryOptimization()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_GRANTED) {
                val  powerManager = getSystemService(POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    AppUtils.writeLogs(
                        "Request ignore battery optimizations (\"1.\" alternative; with package URI) - Entire application: Enabled"
                    )
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }

            }
        }
    }




}