package com.example.callrecording.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.callrecording.MyApplication
import com.example.callrecording.R
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.PrefrenceUtils
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        changeDeviceNameStatus()
        changeRecordServiceStatus()
        changeRecordingAnnouncement()
        //changeAudioSourcePref()

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            try {
                when (key) {
                    getString(R.string.record_service) -> {
                        val serviceStatus = sharedPreferences.getBoolean(key, true)
                        PrefrenceUtils.setServiceStatus(context, serviceStatus)
                        changeRecordServiceStatus()
                    }
                    getString(R.string.audio_source) -> {
                        changeAudioSourcePref()
                    }
                    getString(R.string.outgoing_delay) -> {
                        changeRecordingAnnouncement()
                    }
                    getString(R.string.incoming_delay) -> {
                        changeRecordingAnnouncement()
                    }
                    getString(R.string.announcement_status) -> {
                        changeRecordingAnnouncement()
                    }
                }
            } catch (e: Exception) {
                AppUtils.writeLogs("Setting excetpion = ${e.toString()}")
            }
        }

    }


    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.clear_call_log) -> {
                showDeleteAlertDialog()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }


    private fun changeAudioSourcePref() {
        val audioSourcePref =
            preferenceManager.findPreference<ListPreference>(getString(R.string.audio_source)) as ListPreference
        audioSourcePref.setOnPreferenceChangeListener { preference, newValue ->
            true
        }
        val entry = audioSourcePref.entry
        val entryValue = audioSourcePref.value
        val audioSource = Integer.valueOf(
            PrefrenceUtils.getAudioSource(context)
        )
    }

    private fun changeRecordingAnnouncement() {
            return
        val announcementPref =
            preferenceManager.findPreference<Preference>(getString(R.string.announcement_status)) as Preference
        val isServiceEnabled = PrefrenceUtils.getAnnouncementStatus(context)
        announcementPref.summary = if (isServiceEnabled)
            "Enabled Recording Announcement"
        else
            "Disabled Recording Announcement"


        val incomingDelayAnnouncement =
            preferenceManager.findPreference<ListPreference>(getString(R.string.incoming_delay)) as ListPreference

        incomingDelayAnnouncement.setOnPreferenceChangeListener { preference, newValue ->
            true
        }

        incomingDelayAnnouncement.isEnabled = isServiceEnabled

        val incomingDelay = Integer.valueOf(
            PrefrenceUtils.getIncomingAnnouncementSeconds(context)
        )

        AppUtils.writeLogs("Incoming call delay = $incomingDelay seconds")


        val outgoingDelayAnnouncement =
            preferenceManager.findPreference<ListPreference>(getString(R.string.outgoing_delay)) as ListPreference

        outgoingDelayAnnouncement.setOnPreferenceChangeListener { preference, newValue ->
            true
        }

        outgoingDelayAnnouncement.isEnabled = isServiceEnabled

        val outgoingDelay = Integer.valueOf(
            PrefrenceUtils.getOutgoingAnnouncementSeconds(context)
        )

        AppUtils.writeLogs("Outgoing call delay = $outgoingDelay seconds")


    }


    private fun changeRecordServiceStatus() {
        val servicePref =
            preferenceManager.findPreference<Preference>(getString(R.string.record_service)) as Preference
        val isServiceEnabled = PrefrenceUtils.getServiceStatus(context)
        servicePref.summary = if (isServiceEnabled)
            "Call Recording Service Enabled"
        else
            "Call Recording Service Disabled"
    }

    private fun changeDeviceNameStatus() {
        val devicePreference =
            preferenceManager.findPreference<Preference>(getString(R.string.device_name)) as Preference
        devicePreference.summary = PrefrenceUtils.getDeviceName(context)
        /*val companyPreference =
            preferenceManager.findPreference<Preference>(getString(R.string.company_name)) as Preference
        companyPreference.summary = PrefrenceUtils.getCompanyName(context)*/
    }


    private fun showDeleteAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure to delete all records from device?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->

            lifecycleScope.launch {
                MyApplication
                    .repository.deleteAllRecords()
            }

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // Handle negative button click
        }
        alertDialogBuilder.show()
    }

}