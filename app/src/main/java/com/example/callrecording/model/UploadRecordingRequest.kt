package com.example.callrecording.model

import com.google.gson.annotations.SerializedName


data class UploadRecordingRequest (

    @SerializedName("action"   ) var action   : String? = null,
    @SerializedName("filename" ) var filename : String? = null,
    @SerializedName("mimetype" ) var mimetype : String? = null

)