package com.example.forests.data

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
const val RevGeoCodingAPI_KEY: String = "h27fdmxssx2dxlp369n6ncdc7ach1wui"

interface revGeoCodingService {


    @GET("advancedmaps/v1/$RevGeoCodingAPI_KEY/rev_geocode")
        fun getAddress(
            @Query("lat") lattitude: String,
            @Query("lng") longitude: String

    ): Deferred<revGeoCodingService>

        companion object{
            operator fun invoke(): revGeoCodingService? {
                val requestInterceptor = Interceptor{chain ->
                    val url = chain.request()
                        .url()
                        .newBuilder()
                        .build()



                    val request = chain.request()
                        .newBuilder()
                        .url(url)
                        .build()

                    Log.i("RevGeoCodeAPI",url.toString())
                    return@Interceptor chain.proceed(request)

                }
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(requestInterceptor)
                    .build()

                return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://apis.mapmyindia.com/")
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(revGeoCodingService::class.java)
            }
        }
    }
