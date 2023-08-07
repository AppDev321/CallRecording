package com.example.callrecording.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object ApiClient {

    val liveAppURL= "https://script.google.com/macros/s/AKfycbxcWnK6saoiRM54J6qprATOF2M9PV5wN3MxN6Q8iB602QUCjXXSqdCe19DgFyx9TFC_xQ/"
    val stagingAppURL= "https://script.google.com/macros/s/AKfycbxhqTtvJyWNR30Pt8ZYpHWMGbiMn5jIIs_lMJXi12CKb7tcWl6-y1hQRW7H071xKqcT/"

    lateinit var retrofit: Retrofit
    val client: Retrofit
        get() {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()
            retrofit = Retrofit.Builder()
               .baseUrl(liveAppURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit
        }

}