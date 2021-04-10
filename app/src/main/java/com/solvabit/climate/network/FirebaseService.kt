package com.solvabit.climate.network

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.User
import com.solvabit.climate.registerLogin.ResetPassword.Companion.TAG
import com.solvabit.climate.utility.StatsUtility

public class FirebaseService(var uid: String) {


    suspend fun fetchUser(myCallback: (result:User)-> Unit){
        var database = FirebaseDatabase.getInstance();
        val ref = database.getReference("Users/$uid")
        var user: User = User()
        ref.get().addOnSuccessListener {
            user = it.getValue(User::class.java)!!
            Log.v("FirebaseService", "Fetched User From Firebase ${user}")

            myCallback.invoke(user)

        }.addOnFailureListener {
            Log.v("FirebaseService", "Error getting data")
        }
    }


    fun isnewuser(uid: String , myCallback: (result: Boolean) -> Unit){
        Log.v("FirebaseService","Fetched User has not been initialized")

        var database = FirebaseDatabase.getInstance();
        val ref = database.getReference("Users/$uid/updated")

        ref.get().addOnSuccessListener {
            myCallback.invoke(it as Boolean)
        }.addOnFailureListener {
            Log.v("FirebaseService", "Error getting data")
        }
    }


    fun userStatus(myCallback: (result:String)-> Unit){

        var database = FirebaseDatabase.getInstance();
        val refU = database.getReference("Users/$uid/updated")

        refU.get().addOnSuccessListener {

            var status = false
            if (it.value != null) {
                status = it.value as Boolean
            }
            Log.v("FirebaseService", "User is updated ->> ${status}")

            if (status == true) {
                myCallback.invoke("true")
            } else {
                myCallback.invoke("false")
            }
        }.addOnFailureListener {
            Log.v("FirebaseService", "Error getting data")
            myCallback.invoke("error")
        }
    }


    fun fetchContinuosUpdates(myCallback: (result: User) -> Unit){

        var database = FirebaseDatabase.getInstance();
        val refU = database.getReference("Users/$uid")


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                Log.v("Repository", user.toString())
                myCallback.invoke(user as User)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        refU.addValueEventListener(postListener)
    }


    fun fetchUpdates(myCallback: (result: User) -> Unit){

        var database = FirebaseDatabase.getInstance();
        val refU = database.getReference("Users/$uid")

        refU.get().addOnSuccessListener {

            var user = it.getValue(User::class.java)!!

            Log.v("FirebaseService", "One time fetch update called")

            myCallback.invoke(user)

        }.addOnFailureListener {
            Log.v("FirebaseService", "Error getting data")
        }
    }

    fun fetchStats(myCallback: (result: Stats) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val refU = db.collection("forestCoverYearWiseComparision").document("india").collection("statesComparision").document("bihar")

        refU.get().addOnSuccessListener {

            var data = it.data
            var stats = Stats()
            Log.v("FirebaseService","Stats data fetch update called")
            Log.v("FirebaseService",data.toString())

            if (data != null) {
                stats = StatsUtility().parseForestComparision(uid,data)
            }


            myCallback.invoke(stats)

        }.addOnFailureListener{
            Log.v("FirebaseService", "Error getting stats data")
        }
    }


    fun writeUpdatesForTreesPlanted(user: User) {
        FirebaseDatabase.getInstance().getReference("/Users/$uid")
            .child("treesPlanted").setValue(user.treesPlanted)
    }


    private fun fetchLocationFromFirebase() {

    }

    fun saveUser(uid: String, user: User) {

    }

    fun updateUser(uid: String, user: User) {

    }

    fun onResponse(user: User) {

    }
}
