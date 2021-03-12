package com.solvabit.climate.network;

import android.util.Log
import com.example.forests.data.airQualityDataService
import com.example.forests.data.airQualityResponse.Data

public class FetchAPI(val lattitude:Double, val longitude:Double, val state:String) {


     suspend fun AirQualityData(): Data {
        lateinit var airQualityData: List<Data>
        val apiService = airQualityDataService()
        val response = apiService?.getTreesByCoordinates(lattitude , longitude)?.await()
        if (response != null) {
            airQualityData = response.data
            val aqi= airQualityData[0].aqi.toInt()

            Log.i("AirQualityAPIresponse", response.data.toString())
            return  airQualityData[0]
        }
        return  airQualityData[0]
    }


     suspend fun StateForestData(): Data {
        lateinit var airQualityData: List<Data>
        val apiService = airQualityDataService()
        val response = apiService?.getTreesByCoordinates(lattitude , longitude)?.await()
        if (response != null) {
            airQualityData = response.data
            val aqi= airQualityData[0].aqi.toInt()

            Log.i("AirQualityAPIresponse", response.data.toString())
            return  airQualityData[0]
        }
        return  airQualityData[0]
    }

}
