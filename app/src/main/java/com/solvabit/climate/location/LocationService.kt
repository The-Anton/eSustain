package com.solvabit.climate.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.network.GenericApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LocationService(var uid: String, var locationManager: LocationManager) {

    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var location: Location? = null
    private lateinit var locationListener:LocationListener
    var locationListenerCount =0
    var database = FirebaseDatabase.getInstance();


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, myCallback: (result: Map<String, Double>) -> Unit) {
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {

                locationGps = p0
                location = locationGps

                Log.v("LocationService", " GPS: ${locationGps}")
                myCallback.invoke(mapOf<String, Double>("latitude" to locationGps!!.latitude, "longitude" to locationGps!!.longitude))
                locationManager.removeUpdates(locationListener)

            }

        }

        if (hasGps || hasNetwork) {

            if (hasGps) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0F,
                        locationListener
                )

//
//                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                if (localGpsLocation != null) locationGps = localGpsLocation

            }
        } else {
            Toast.makeText(context, "Please turn on your location or internet", Toast.LENGTH_SHORT).show()

        }

    }


}