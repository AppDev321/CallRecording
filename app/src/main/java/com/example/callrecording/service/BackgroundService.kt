package com.example.callrecording.service

import android.content.Context
import android.util.Base64
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import com.example.callrecording.MyApplication
import com.example.callrecording.database.tables.PhoneCall
import com.example.callrecording.model.GeneralResponse
import com.example.callrecording.retrofit.ApiClient
import com.example.callrecording.retrofit.ApiInterface
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.AppUtils.getMimeType
import com.example.callrecording.utils.PrefrenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BackgroundService(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
val context = context

    companion object {
        lateinit var phoneCall: PhoneCall
    }

    override suspend fun doWork(): Result {
        AppUtils.writeLogs("Starting background work")
        // val callData = inputData.getString("data") as String
        val callDetailData = phoneCall //Json.decodeFromString<PhoneCall>(callData)

        AppUtils.writeLogs("call detail = $callDetailData")
        uploadAudioFileToDrive(callDetailData, callDetailData.filePath.toString())
        AppUtils.writeLogs("Background work complete")
        return Result.success()
    }


    private suspend fun uploadAudioFileToDrive(callData: PhoneCall, filePath: String) {
        val record = File(filePath)
        val extensiton =  record.extension
        val getCurrentDateTime = AppUtils.getCurrentSystemTimeDate(System.currentTimeMillis())
       // val fileName = callData.number+"_"+getCurrentDateTime+"."+extensiton
       val fileName = AppUtils.getCurrentDateTime() +"$$"+ PrefrenceUtils.getDeviceName(context)+"."+extensiton
        withContext(Dispatchers.IO)
        {
            try {
                val fileData = Base64.encodeToString(record.readBytes(), Base64.NO_WRAP)
                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.uploadBase64Data(
                    action = "upload_record",
                    fileName = fileName,
                    mimeType = getMimeType(record).toString(),
                    base64File = fileData,
                    deviceName = PrefrenceUtils.getDeviceName(context),
                    companyName = PrefrenceUtils.getCompanyName(context)
                )
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }
                AppUtils.writeLogs("Drive api response = $response")
                //sendErrorLogs("Drive api response = $response")
                if (response.status == true) {
                    callData.upload = true
                    callData.driveLink = response.message
                } else {
                    callData.upload = false

                }
                insertDataIntoDatabase(callData)
                addDataInExcelSheet(callData)
            } catch (e: Exception) {
                AppUtils.writeLogs("Exception= $e")
                AppUtils.writeLogs("Failed to upload audio file: ${e.message}")
               // sendErrorLogs("Failed to upload audio file: ${e.message}")
            }
        }
    }


    private suspend fun addDataInExcelSheet(callData: PhoneCall) {

        withContext(Dispatchers.IO)
        {
            try {


                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.addCallDetailMileStone2(
                   // action = "add_call_record",
                    action= "add_call_detail",
                    companyName = PrefrenceUtils.getCompanyName(context),
                    deviceName = PrefrenceUtils.getDeviceName(context),
                    callerNumber = callData.number.toString(),
                    callDate = callData.date.toString(),
                    callTime = callData.time.toString(),
                    callType = callData.type.toString(),
                    duration = callData.duration.toString(),
                    driveLink = callData.driveLink.toString(),


                    )
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }


            } catch (e: Exception) {
                AppUtils.writeLogs( "Failed to upload audio file: ${e.message}")
            }
        }
    }


    private suspend fun insertDataIntoDatabase(callData: PhoneCall) {
        withContext(Dispatchers.IO)
        {
            MyApplication.repository.insert(callData)
        }

    }


    private fun sendErrorLogs(msg: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.addError(errorMessage = msg)
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }


            } catch (e: Exception) {

            }
        }

    }

}