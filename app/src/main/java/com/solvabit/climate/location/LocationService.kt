package com.solvabit.climate.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.solvabit.climate.database.User
import kotlin.collections.Map as Map

class LocationService(var uid:String, var locationManager: LocationManager) {

    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    var database = FirebaseDatabase.getInstance();


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context): Map<String, Double> {
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    object : LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            locationGps = p0

                            locationUpdate(locationGps!!)
                        }
                    })

                val localGpsLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    object : LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            locationNetwork = p0
                            locationUpdate(locationGps!!)

                        }

                    })

                val localNetworkLocation =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    locationUpdate(locationGps!!)

                } else {
                    locationUpdate(locationGps!!)

                }
            }

        } else {
            //startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            Toast.makeText(context, "Please turn on your location or internet", Toast.LENGTH_SHORT).show()

        }

        return  mapOf<String,Double>("latitude" to locationGps!!.latitude, "longitude" to locationGps!!.longitude)
    }



    fun locationUpdate(locationGps:Location){

        val ref = database.getReference("Users/$uid")
        val refU = database.getReference("Users/$uid")


        val cordinates = listOf<Double>(locationGps.latitude,locationGps.longitude)

        val childUpdates = mapOf<String,List<Double>>(
            "location" to cordinates
        )


        ref.updateChildren(
            childUpdates
        )
            .addOnSuccessListener {
                Log.v("FirebaseService", "Updated Location on Firebase")

            }
            .addOnFailureListener {
                Log.v("FirebaseService", "Failed to update location on Firebase")

            }



        refU.updateChildren(
                mapOf("locationUpdated" to true)
        )
                .addOnSuccessListener {
                    Log.v("FirebaseService", "Updated Location Status on Firebase")

                }
                .addOnFailureListener {
                    Log.v("FirebaseService", "Failed to update location status on Firebase")

                }
    }


//
//    fun getLocationFromFirebase(){
//
//        var database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);
//        val ref = database.getReference("Users/$uid")
//        val refU = database.getReference("Users/$uid/updated")
//
//        val childUpdates = mapOf<String, Double>(
//            "longitude" to locationGps!!.longitude,
//            "latitude" to locationGps!!.latitude
//        )
//
//        ref.get().addOnSuccessListener {
//            user = it.getValue(User::class.java)!!
//            Log.v("FirebaseService","Fetched User From Firebase ${user}")
//
//            onResponse(user)
//        }.addOnFailureListener{
//            Log.v("FirebaseService", "Error getting data")
//        }
//
//    }

}