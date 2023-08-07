package com.example.callrecording.retrofit

import com.example.callrecording.model.GeneralResponse
import com.example.callrecording.utils.AppUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @POST("exec")
    @FormUrlEncoded
    fun addDevice(
        @Field("action") action: String? = "add_device",
        @Field("vDeviceName") deviceName: String
    ): Call<GeneralResponse>


    @POST("exec")
    @FormUrlEncoded
    fun addDeviceMileStone2(
        @Field("action") action: String? = "add_new_device",
        @Field("vDeviceName") deviceName: String,
        @Field("vCompanyName") companyName:String
    ): Call<GeneralResponse>



    @POST("exec")
    @FormUrlEncoded
    fun addError(
        @Field("action") action: String? = "add_error",
        @Field("vError") errorMessage: String
    ): Call<GeneralResponse>



    @POST("exec")
    @FormUrlEncoded
    fun uploadBase64Data(
        @Field("action") action: String,
        @Field("vFileName") fileName: String,
        @Field("vMimeType") mimeType: String,
        @Field("vFile") base64File: String,
        @Field("vDeviceName") deviceName: String ,
        @Field("vCompanyName") companyName:String
    ): Call<GeneralResponse>



   // @Multipart
    @POST("exec")
    fun uploadRecording(
       @Header("Content-Type")  contentType:String,
       @Header("Content-Disposition")  contentDisposition:String,
        @Query("query") action: String,

        @Body file: RequestBody
    ): Call<GeneralResponse>




    @POST("exec")
    @FormUrlEncoded
    fun addCallRecordInExcelSheet(
        @Field("action") action: String,
        @Field("vSheetName") deviceName: String,
        @Field("vNumber") callerNumber:String,
        @Field("vType") callType: String,
        @Field("vDate") callDate: String,
        @Field("vTime") callTime: String,
        @Field("vDuration") duration: String,
        @Field("vLink") driveLink: String,

    ): Call<GeneralResponse>



    @POST("exec")
    @FormUrlEncoded
    fun addCallDetailMileStone2(
        @Field("action") action: String,
        @Field("vDeviceName") deviceName: String ,
        @Field("vCompanyName") companyName:String,
        @Field("vNumber") callerNumber:String,
        @Field("vType") callType: String,
        @Field("vDate") callDate: String,
        @Field("vTime") callTime: String,
        @Field("vDuration") duration: String,
        @Field("vLink") driveLink: String,

        ): Call<GeneralResponse>

}