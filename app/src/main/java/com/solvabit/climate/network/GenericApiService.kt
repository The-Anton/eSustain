package com.solvabit.climate.network

import android.util.Log
import com.example.forests.data.parametersDataService

class GenericApiService(val uid: String, val lattitude: Double, val longitude: Double) {


    suspend fun newUser(callback: (result: List<String>) -> Unit) {


        lateinit var response: parametersData
        Log.v("NewUser", "started new user initiating")

        val apiService = parametersDataService()

        try {
            val response = apiService?.getData(uid, lattitude, longitude)?.await()

            if (response?.apistatus == true) {
                Log.v("NewUser", response.toString())
                callback(listOf("true", response.country))
            } else {
                Log.v("NewUser", "NO data fetched")
                callback(listOf("false", "invalid"))
            }
        } catch (e: Exception) {
            Log.v("API Exception", e.toString())
            callback(listOf("false", "invalid"))
            //reFetch()
        }


    }

}
