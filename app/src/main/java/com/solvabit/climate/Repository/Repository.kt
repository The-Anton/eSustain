package com.solvabit.climate.Repository;

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.forests.data.airQualityDataService
import com.example.forests.data.airQualityResponse.AirQualityData
import com.example.forests.data.airQualityResponse.Data
import com.google.firebase.database.*
import com.solvabit.climate.dataModel.ForestData
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.network.FetchAPI
import com.solvabit.climate.network.FirebaseService

public class Repository constructor(val dao: UserDao,val uid:String) {

    private  val firebaseService = FirebaseService(dao,uid)

     suspend fun getUser(myCallback: (result: User) -> Unit) {
        val userCount = dao.hasUser(uid)
        Log.v("Repository", "User count : ${userCount}")

        if( userCount == 1) {
            Log.v("Repository", "User is present in room database")
            val user = dao.getUserByUID(uid)
            Log.v("Repository", "User updated to ${user}")
            myCallback.invoke(user)
        }
        else{
            Log.v("Repository", "User is not present in room database... fetchUserData is called")

            val user = fetchUserData{
                result ->
                Log.v("Repository", "User updated to ${result}")
                dao.insert(result)
                Log.i("FirebaseService", "User INSERTED")
                myCallback.invoke(dao.getUserByUID(uid))
            }
            return user

        }

    }

     fun fetchUserData(myCallback: (result:User)-> Unit){
        Log.v("Repository", "fetchUserData called")

        //if(firebaseService.isUserDataPresent() == true) {
            firebaseService.fetchUser { result ->
                myCallback.invoke(result)
            }
        //}
//        else{
//            var user:User = CalculateParameters()
//            dao.insert(user)
//            return dao.getUserByUID(uid)
//        }

    }



//    suspend fun CalculateParameters(): User {
//
//        lateinit var forestData: ForestData
//        lateinit var addressLocationData: List<Data>
//        val apiService = airQualityDataService()
//
//        //val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
//       // var LATTITUDE = //sharedPreferences.getString("lat", " ").toString()
//       // var LONGITUDE = //sharedPreferences.getString("lon", " ").toString()
//       // var STATE = //sharedPreferences.getString("state", " ").toString()
//
//        val airQualityData: Data = FetchAPI(LATTITUDE,LONGITUDE,STATE).AirQualityData()
//
//        var user:User = FirebaseService(dao).fetchUser()
//
//        user.lattitude = LATTITUDE
//        user.longitude = LONGITUDE
//        user.normalizedScore = airQualityData.aqi.toInt()
//        user.rating = "Rookie"
//        return user
//    }
//

}

