package com.example.forests.data

import com.example.forests.data.stateForestDataResponse.StateForestData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.solvabit.climate.network.parametersData
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val KEY = "579b464db66ec23bdd00000157bc862d9f2146d84b764d388c4b7319"

interface parametersDataService {
    @GET("/newuser")
    fun getData(
        @Query("uid") uid: String = "0",
        @Query("latitude") latitude: Double = 0.0,
        @Query("longitude") longitude: Double = 0.0
    ): Deferred<parametersData>

    companion object{
        operator fun invoke(): parametersDataService? {
            val requestInterceptor = Interceptor{chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
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
                .baseUrl("https://esustain.herokuapp.com")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(parametersDataService::class.java)
        }
    }
}