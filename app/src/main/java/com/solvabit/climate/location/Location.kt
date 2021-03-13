package com.solvabit.climate.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.example.forests.data.airQualityDataService
import com.example.forests.data.airQualityResponse.Data
import com.example.forests.data.revGeoCodingResponse.AddressLocationData
import com.example.forests.data.revGeoCodingResponse.Result
import com.example.forests.data.revGeoCodingService
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.activity.MainActivity
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.network.FirebaseService
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val PERMISSION_REQUEST = 10
class Location : AppCompatActivity() {


    lateinit var locationManager: LocationManager
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var uid:String
    private var updatedToFirebase=false
    private lateinit var location: Map<String,Double>
    private var isLocationAlreadyAvailabe  = false
    var gpsStatus: Boolean = false
    var stateList:List<String> = listOf("andhra pradesh", "arunachal pradesh","assam","bihar","chhattisgarh","delhi","new delhi","goa","gujarat","haryana","himachal pradesh","jammu and kashmir","jharkhand","karnataka", "kerala","madhya pradesh","maharashtra","meghalaya","manipur","mizoram","nagaland","orissa","punjab","rajasthan","sikkim","tamil nadu","telangana","tripura","uttar pradesh","uttarakhand","west bengal","andaman and nicobar","chandigarh","dadar nagar haveli","daman and diu","lakshadweep","puducherry")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        uid = FirebaseAuth.getInstance().uid.toString()

        val button = findViewById<CircularProgressButton>(R.id.button)
        val cityEditText = findViewById<EditText>(R.id.editTextLocationName)


        val instance = UserDatabase.getInstance(this@Location)
        val dao = instance.userDao()

        FirebaseService(dao,uid).isLocationPresent(){
            result -> isLocationAlreadyAvailabe = result

            if(isLocationAlreadyAvailabe){
                Toast.makeText(this, "Location Is Already on firebase: ${isLocationAlreadyAvailabe}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission(permissions)) {
                        getLocation(uid,locationManager)
                    } else {
                        requestPermissions(permissions, PERMISSION_REQUEST)
                    }
                } else {
                        getLocation(uid,locationManager)
                }
            }
        }

        button.setOnClickListener {
            button.startAnimation()
                if(!cityEditText.text.isEmpty()){
                    var flag=1
                    var state = ""
                    var temp  = cityEditText.text.toString()

                    for(i in temp){
                        if(!i.isLetter()){
                            if(!i.isWhitespace()){
                                Toast.makeText(this, "Please Enter correct city without any digit and special caracters", Toast.LENGTH_SHORT).show()
                                button.revertAnimation();
                                flag=0;
                            }

                        }
                    }

                    for (c in temp) {
                        state += when {
                            c.isUpperCase() -> c.toLowerCase()
                            c.isWhitespace() -> " "
                            else -> c
                        }
                    }
                    Log.d("Updated", updatedToFirebase.toString())

                    Log.d("state", state)

                    if(state in stateList) {

                            if(flag==1 && state!="State" && updatedToFirebase){
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                                 startActivity(intent)
                            }else{
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                button.revertAnimation();

                            }
                    }else{
                                Toast.makeText(this, "Please Enter correct city without any digit and special caracters", Toast.LENGTH_SHORT).show()
                                button.revertAnimation();
                            }


                }else{
                    Toast.makeText(this, "Please Enter your city", Toast.LENGTH_SHORT).show()
                    button.revertAnimation();
                }
            }

        }





    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }


    fun getLocation(uid:String,locationManager: LocationManager){
        LocationService(uid,locationManager).getLocation(this) { result ->
            location = result
        }
        Log.v("Coordinates", "Location from phone : ${location}")

        LocationService(uid,locationManager).locationUpdate(location) { result ->
            updatedToFirebase = result
//
        }

        Log.v("Coordinates", "Location updated on firebase : ${updatedToFirebase}")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                LocationService(uid,locationManager).getLocation(this, {
                    result ->  location = result

                    Log.v("Coordinates", "Location from phone : ${location}")

                    LocationService(uid,locationManager).locationUpdate(location,{
                        result -> updatedToFirebase = result
                        Log.v("Coordinates", "Location updated on firebase : ${updatedToFirebase}")

                    })


                })
            }
    }
}
