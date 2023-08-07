package com.example.callrecording.viewmodel

import android.content.Context
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callrecording.model.DeviceDetail
import com.example.callrecording.model.GeneralResponse
import com.example.callrecording.retrofit.ApiClient
import com.example.callrecording.retrofit.ApiInterface
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.AppUtils.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AppViewModel : ViewModel() {
    private val _snackMessage = MutableSharedFlow<String>()
    val snackMessage = _snackMessage.asSharedFlow()


    private val _apiResult = MutableSharedFlow<Result<GeneralResponse>>()
    val apiResult = _apiResult.asSharedFlow()


    fun showMessage(message: String) {
        viewModelScope.launch {
            _snackMessage.emit(message)
        }
    }


    fun addDeviceOnSheet(name: String,companyName: String) {
        viewModelScope.launch {
            _apiResult.emit(Result.Loading)
            try {
                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.addDeviceMileStone2(deviceName = name, companyName = companyName)
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }
                val deviceDetails = DeviceDetail(deviceName = name,companyName=companyName)
                response.data = arrayListOf( deviceDetails)
                _apiResult.emit(Result.Success(response))
            } catch (e: Exception) {
                _apiResult.emit(Result.Error(e))
            }
        }
    }




    /* fun uploadCallRecord(context: Context, record : File) {



         val queryParams = UploadRecordingRequest("upload_record",record.name, getMimeType(record))

         val gson = Gson()
         val jsonString = gson.toJson(queryParams)
         println(jsonString)


        *//* val requestFile = RequestBody.create(
            context.contentResolver.getType(Uri.parse(record.absolutePath))
                ?.let { MediaType.parse(it) },
            record
        )*//*
       // val body = MultipartBody.Part.createFormData("file", record.name, requestFile)

        val requestBody = //record.asRequestBody("application/octet-stream")
        RequestBody.create(
            MediaType.parse("application/octet-stream"),
            record
        )


        viewModelScope.launch {
            _apiResult.emit(Result.Loading)
            try {
                val apiInterface = ApiClient.client.create(ApiInterface::class.java)
                val call = apiInterface.uploadRecording( file = requestBody, action = jsonString, contentType = "application/octet-stream", contentDisposition =  "attachment; filename=\"sample.jpg\"")
                val response = withContext(Dispatchers.IO) {
                    call.execute().body() as GeneralResponse
                }
                _apiResult.emit(Result.Success(response))
            } catch (e: Exception) {
                _apiResult.emit(Result.Error(e))
            }
        }
    }
*/

}


sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}