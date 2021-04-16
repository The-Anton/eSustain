package com.solvabit.climate.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.activity.MainActivity
import com.solvabit.climate.activity.UnavailableActivity
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.network.FirebaseService
import com.solvabit.climate.network.GenericApiService
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


const val PERMISSION_REQUEST = 10

class Location : AppCompatActivity() {


    lateinit var locationManager: LocationManager
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var uid: String
    private var updatedToFirebase = false
    private lateinit var location: Map<String, Double>
    var gpsStatus: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        //button.visibility = View.INVISIBLE

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        uid = FirebaseAuth.getInstance().uid.toString()

        val bundle = this.intent.extras

        if (bundle != null) {
            var fakeLatitude = bundle.getSerializable("latitude")
            var fakeLongtitude = bundle.getSerializable("longitude")

            initiateNewUser(fakeLatitude as Double, fakeLongtitude as Double)

        }else{
            val instance = UserDatabase.getInstance(this@Location)
            val dao = instance.userDao()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


            var editor: SharedPreferences.Editor? = sharedPreferences.edit()
            editor?.putString("noOfTimesLocationUpdated", 0.toString())
            editor?.putString("locationListenerCount", 0.toString())
            editor?.apply()


            checkStatus(dao)

            randomButton.setOnClickListener{
                randomButton.startAnimation()
                //initiateNewUser(location["latitude"]!!.toDouble(),location["longitude"]!!.toDouble())
                checkStatus(dao)
            }


            Handler().postDelayed(
                {
                    locationStatusText.text =
                        "It is taking more than usual... Please wait for some time!!"
                }, 20000
            )
        }

    }



    fun checkStatus(dao: UserDao){
        FirebaseService( uid).userStatus { result ->
            if(result.equals("error")){
                Log.v("Firebase", "Got error ")
//                button.revertAnimation()
                randomButton.visibility = View.VISIBLE
            }else{
                if(result.equals("true")){
                    Log.v("Firebase", "Got true")
                    startMainActivity()
                }else{
                    Log.v("Firebase", "Got false")
                    runnableHandler()
                }
            }
        }
    }
    fun runnableHandler() {
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (locationEnabled()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkPermission(permissions)) {
                            getUserLocation(uid, locationManager)

                        } else {
                            requestPermissions(permissions, PERMISSION_REQUEST)
                        }
                    } else {
                        getUserLocation(uid, locationManager)
                    }
                } else {
                    Toast.makeText(
                        this@Location,
                        "Please turn on your location ",
                        Toast.LENGTH_SHORT
                    ).show()
                    mainHandler.postDelayed(this, 3000)
                }


            }
        })

    }

    fun startMainActivity() {
        Log.v("Intent", "Stared Intent")

        val intent = Intent(this, MainActivity::class.java)
        Log.v("Intent", "Stared Intent2")

        startActivity(intent)
    }

    fun startUnavailableActivity() {
        Log.v("Intent", "Stared Intent")

        val intent = Intent(this, UnavailableActivity::class.java)
        Log.v("Intent", "Stared Intent2")
        

        startActivity(intent)
    }
    private fun locationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return gpsStatus
    }


    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }


    fun getUserLocation(uid: String, locationManager: LocationManager) {

        LocationService(uid, locationManager).getLocation(this) { result ->
            location = result
            var latitude = location["latitude"]!!.toDouble()
            var longitude = location["longitude"]!!.toDouble()
            Log.v("Coordinates", "Location from phone : ${location}")

            initiateNewUser(latitude, longitude)


        }


    }


    fun initiateNewUser(latitude: Double, longitude: Double) {
        randomButton.startAnimation()
        GlobalScope.launch {
            GenericApiService(uid, latitude, longitude).newUser {

                Log.v("Coordinates", "Made api call ")

                if (it[0].equals("true")) {
                    Log.v("Coordinates", "Response : ${it}")
                    if (it[1].equals("India")){
                        startMainActivity()
                    }else{
                        startUnavailableActivity()
                    }

                }else{
                    Log.v("Coordinates", "Retrying...... : ${it}")
                   
                    initiateNewUser(latitude, longitude)
                        //getUserLocation(uid,locationManager)
                }
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                        permissions[i]
                    )
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
          if (allSuccess) {
              Log.v("Permission", "Got permission")
              getUserLocation(uid, locationManager)

          }
//                LocationService(uid, locationManager).getLocation(this, { result ->
//                    location = result
//                    var latitude = location["latitude"]!!.toDouble()
//                    var longitude = location["longitude"]!!.toDouble()
//
//                    Log.v("Coordinates", "Location from phone : ${location}")
//                    initiateNewUser(latitude,longitude)
//
//                })
        }
    }
}
