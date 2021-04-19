package com.solvabit.climate.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber


class LocationService(var context: Context) {

    private var locationGps: Location? = null
    private var location: Location? = null
    private lateinit var locationListener: LocationListener
    var database = FirebaseDatabase.getInstance()
    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var gpsStatus: Boolean = false
    var noOfRetry = 0
    lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    fun getLocation(myCallback: (result: Map<String, Double>) -> Unit) {
        locationManager =
            context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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


    fun locationEnabled(): Boolean {
        locationManager =
            context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return gpsStatus
    }

    fun checkPermission(): Boolean {

        var allSuccess = true
        for (i in permissions.indices) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permissions[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allSuccess = false
            }
        }
        return allSuccess
    }


}