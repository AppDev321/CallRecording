package com.example.callrecording.model

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("status"  ) var status  : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : ArrayList<Any> = arrayListOf()
)

data class DeviceDetail (
    @SerializedName("device_name"         ) var deviceName         : String?    = null,
    @SerializedName("company_name" ) var companyName : String? = null,

)

data class IngredientData (
    @SerializedName("id"         ) var id         : Int?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("cp"      ) var cp      : Double? = null,
    @SerializedName("price") var price      : Double? = null,
    @SerializedName("Qty")var qty      : Int? = null,
)