package com.example.forests.data

import com.example.forests.data.airQualityResponse.AirQualityData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val AirQualityAPI_KEY: String = "fe3cc9eeea474df0af9999424550bdee"

interface airQualityDataService {
    @GET("v2.0/current/airquality")
    fun getTreesByCity(
        @Query("city") city: String
    ): Deferred<AirQualityData>

    @GET("v2.0/current/airquality")
    fun getTreesByCoordinates(
            @Query("lat") lattitude: Double,
            @Query("lon") longitude: Double
    ): Deferred<AirQualityData>

    companion object{
        operator fun invoke(): airQualityDataService? {
            val requestInterceptor = Interceptor{chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", AirQualityAPI_KEY)
                    .build()



                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)

            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.weatherbit.io/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(airQualityDataService::class.java)
        }
    }
}