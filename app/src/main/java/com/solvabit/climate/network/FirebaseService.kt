package com.solvabit.climate.network

import android.util.Log
import com.google.firebase.database.*
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

public class FirebaseService(var dao:UserDao, var uid: String) {


    fun fetchUser(myCallback: (result:User)-> Unit){
        var database = FirebaseDatabase.getInstance();
        val ref = database.getReference("Users/$uid")
        var user:User = User()

                ref.get().addOnSuccessListener {
                    user = it.getValue(User::class.java)!!
                    Log.v("FirebaseService","Fetched User From Firebase ${user}")

                    myCallback.invoke(user)
                }.addOnFailureListener{
                    Log.v("FirebaseService", "Error getting data")
                }

    }


    fun onResponse(user: User) {

    }

    fun isLocationPresent(myCallback: (result:Boolean)-> Unit){
        var database = FirebaseDatabase.getInstance();
        val refU = database.getReference("Users/$uid/locationUpdated")

        refU.get().addOnSuccessListener {
            Log.v("FirebaseService","Location updated on Firebase ->> ${it}")

            var status = it.value as Boolean
            Log.v("FirebaseService","Location updated on Firebase ->> ${status}")

            myCallback.invoke(status)
        }.addOnFailureListener{
            Log.v("FirebaseService", "Error getting data")
        }
    }

    private fun fetchLocationFromFirebase() {

    }

    fun saveUser(uid: String,user:User){

    }

    fun updateUser(uid: String,user: User){

    }

}
