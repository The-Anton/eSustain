package com.solvabit.climate.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber


class LocationService(var uid: String, var locationManager: LocationManager) {

    private var locationGps: Location? = null
    private var location: Location? = null
    private lateinit var locationListener: LocationListener
    var database = FirebaseDatabase.getInstance()


    @SuppressLint("MissingPermission")
    fun getLocation(context: Context, myCallback: (result: Map<String, Double>) -> Unit) {
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {

                locationGps = p0
                location = locationGps

                Timber.v("LocationService %s", " GPS: ${locationGps}")
                myCallback.invoke(
                    mapOf<String, Double>(
                        "latitude" to locationGps!!.latitude,
                        "longitude" to locationGps!!.longitude
                    )
                )
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

            }
        } else {
            Toast.makeText(context, "Please turn on your location or internet", Toast.LENGTH_SHORT)
                .show()

        }

    }


}