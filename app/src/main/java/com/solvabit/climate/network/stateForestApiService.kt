package com.example.forests.data

import com.example.forests.data.stateForestDataResponse.StateForestData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "579b464db66ec23bdd00000157bc862d9f2146d84b764d388c4b7319"

interface stateForestApiService {
    @GET("4b573150-4b0e-4a38-9f4b-ae643de88f09")
    fun getTrees(
        @Query("format") docFormat: String = "json",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 5,
        @Query("filter") filter: String
    ): Deferred<StateForestData>

    companion object{
        operator fun invoke(): stateForestApiService? {
            val requestInterceptor = Interceptor{chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api-key", API_KEY)
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
                .baseUrl("https://api.data.gov.in/resource/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(stateForestApiService::class.java)
        }
    }
}