package com.solvabit.climate.network

import android.util.Log
import com.example.forests.data.parametersDataService
import com.google.firebase.database.*
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

public class FirebaseService(var dao:UserDao, var uid: String) {


     suspend fun fetchUser(myCallback: (result:User)-> Unit){
        var database = FirebaseDatabase.getInstance();
        val ref = database.getReference("Users/$uid")
        var user:User = User()
         newapi(uid)
                ref.get().addOnSuccessListener {
                    user = it.getValue(User::class.java)!!
                    Log.v("FirebaseService","Fetched User From Firebase ${user}")

                    myCallback.invoke(user)

                }.addOnFailureListener{
                    Log.v("FirebaseService", "Error getting data")
                }
    }






    fun isnewuser(uid: String , myCallback: (result: Boolean) -> Unit){
        Log.v("FirebaseService","Fetched User has not been initialized")
        var database = FirebaseDatabase.getInstance();
        val ref = database.getReference("Users/$uid/updated")

        ref.get().addOnSuccessListener {
            myCallback.invoke(it as Boolean)
        }.addOnFailureListener{
            Log.v("FirebaseService", "Error getting data")
        }
    }




    suspend fun newapi(uid: String) {
        lateinit var parametersData: parametersData
        Log.v("NewUser","started new user initiating")

        val apiService = parametersDataService()
        val response = apiService?.getData(uid,30.3752 , 76.7821)?.await()
        if (response != null) {
            Log.v("NewUser", response.toString())
            //callback(true)
        }else{
            Log.v("NewUser", "NO data fetched")
            //callback(false)
        }

    }



    fun userStatus(myCallback: (result:String)-> Unit){
        var database = FirebaseDatabase.getInstance();
        val refU = database.getReference("Users/$uid/updated")

        refU.get().addOnSuccessListener {

            var status = it.value as Boolean
            Log.v("FirebaseService","User is updated ->> ${status}")

            if(status==true){
                myCallback.invoke("true")
            }else{
                myCallback.invoke("false")
            }
        }.addOnFailureListener{
            Log.v("FirebaseService", "Error getting data")
            myCallback.invoke("error")
        }
    }

    private fun fetchLocationFromFirebase() {

    }

    fun saveUser(uid: String,user:User){

    }

    fun updateUser(uid: String,user: User){

    }

    fun onResponse(user: User) {

    }
}
