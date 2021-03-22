package com.solvabit.climate.network;

import android.util.Log
import com.example.forests.data.airQualityDataService
import com.example.forests.data.airQualityResponse.Data
import com.example.forests.data.parametersDataService

public class GenericApiService(val uid: String, val lattitude: Double, val longitude: Double) {


    suspend fun AirQualityData(): Data {
        lateinit var airQualityData: List<Data>
        val apiService = airQualityDataService()
        val response = apiService?.getTreesByCoordinates(lattitude, longitude)?.await()
        if (response != null) {
            airQualityData = response.data
            val aqi = airQualityData[0].aqi.toInt()

            Log.i("AirQualityAPIresponse", response.data.toString())
            return airQualityData[0]
        }
        return airQualityData[0]
    }


    suspend fun StateForestData(): Data {
        lateinit var airQualityData: List<Data>
        val apiService = airQualityDataService()
        val response = apiService?.getTreesByCoordinates(lattitude, longitude)?.await()
        if (response != null) {
            airQualityData = response.data
            val aqi = airQualityData[0].aqi.toInt()

            Log.i("AirQualityAPIresponse", response.data.toString())
            return airQualityData[0]
        }
        return airQualityData[0]
    }


    suspend fun newUser(callback: (result: Boolean) -> Unit) {


        lateinit var parametersData: parametersData
        Log.v("NewUser", "started new user initiating")

        val apiService = parametersDataService()

        try {
            val response = apiService?.getData(uid, lattitude, longitude)?.await()
            if (response != null || response?.status == true) {
                Log.v("NewUser", response.toString())
                callback(true)
            } else {
                Log.v("NewUser", "NO data fetched")
                callback(false)
            }
        }catch (e:Exception){
            Log.v("API Exception", e.toString())
            callback(false)
        }




    }

}
