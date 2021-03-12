package com.solvabit.climate.network

import android.util.Log
import com.google.firebase.database.*
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

public class FirebaseService(var dao:UserDao, var uid: String) {
    //val uid = "PdZV87UWm9QMi4KeR9n4gMiQ23f1"//FirebaseAuth.getInstance().uid
    

    suspend fun fetchUser(){
        var database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        val ref = database.getReference("Users/$uid")
        var user:User = User()
        suspendCancellableCoroutine<User> {
             try {
                ref.get().addOnSuccessListener {
                    user = it.getValue(User::class.java)!!
                    Log.v("FirebaseService","Fetched User From Firebase ${user}")

                    onResponse(user)
                }.addOnFailureListener{
                    Log.v("FirebaseService", "Error getting data")
                }
            }catch (e:Exception){
                 Log.v("FirebaseService", "courutine error : ${e}")

             }
        }

    }


    fun onResponse(user: User) {
        dao.insert(user)
        Log.i("FirebaseService","User INSERTED")

    }



    fun isLocationPresent(myCallback: (result:Boolean)-> Unit){
        var database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
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

    private fun fetchLocationFromFirebase(callback: Callback) {

    }

    fun saveUser(uid: String,user:User){

    }

    fun updateUser(uid: String,user: User){

    }


    interface Callback {
        fun onSuccess(status: Boolean):Boolean
    }
}