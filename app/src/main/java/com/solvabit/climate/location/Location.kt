package com.solvabit.climate.location

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.solvabit.climate.R
import com.solvabit.climate.fragment.LocationPermissionInfo
import com.solvabit.climate.fragment.LocationRequest
import com.solvabit.climate.fragment.ProcessLocation

const val PERMISSION_REQUEST = 10

class Location : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val locationRequest = LocationRequest()
        val locationProcess = ProcessLocation()
        val locationPermissionInfo = LocationPermissionInfo()
        val locationService = LocationService(this)

        if (!locationService.checkPermission()) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLocationFragment, locationPermissionInfo)
            transaction.commit()

        } else if (!locationService.locationEnabled()) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLocationFragment, locationRequest)
            transaction.commit()
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLocationFragment, locationProcess)
            transaction.commit()

        }

        //        if (bundle != null) {
//            val fakeLatitude = bundle.getSerializable("latitude")
//            val fakeLongtitude = bundle.getSerializable("longitude")
//
//            initiateNewUser(fakeLatitude as Double, fakeLongtitude as Double)
//
//        } else {
//            val instance = UserDatabase.getInstance(this@Location)
//            val dao = instance.userDao()
//            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//
//
//            val editor: SharedPreferences.Editor? = sharedPreferences.edit()
//            editor?.putString("noOfTimesLocationUpdated", 0.toString())
//            editor?.putString("locationListenerCount", 0.toString())
//            editor?.apply()
//
//
//            checkStatus(dao)
//
//            randomButton.setOnClickListener {
//                randomButton.startAnimation()
//                checkStatus(dao)
//            }
//
//
//            Handler().postDelayed(
//                {
//                    locationStatusText.text =
//                        "It is taking more than usual... Please wait for some time!!"
//                }, 20000
//            )
//        }

    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST) {
//            var allSuccess = true
//            for (i in permissions.indices) {
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    allSuccess = false
//                    val requestAgain =
//                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
//                            permissions[i]
//                        )
//                    if (requestAgain) {
//                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Go to settings and enable the permission",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//            if (allSuccess) {
//                Timber.tag("Permission").v("Got permission")
//                getUserLocation(uid, locationManager)
//
//            }
//
//        }
//    }
}
