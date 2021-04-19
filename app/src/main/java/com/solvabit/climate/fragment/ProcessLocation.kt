package com.solvabit.climate.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.solvabit.climate.R
import com.solvabit.climate.activity.MainActivity
import com.solvabit.climate.activity.UnavailableActivity
import com.solvabit.climate.location.FusedLocationService
import com.solvabit.climate.location.LocationService
import com.solvabit.climate.network.GenericApiService
import kotlinx.android.synthetic.main.fragment_process_location.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ProcessLocation : Fragment() {


    lateinit var v: View
    lateinit var locationService: LocationService
    private lateinit var uid: String
    var noOfRetry = 0
    var buttonVisible = false
    var latitude = 0.0
    var longitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_process_location, container, false)
        uid = FirebaseAuth.getInstance().uid.toString()
        locationService = context?.let { LocationService(it.applicationContext) }!!


        val bundle = activity?.intent?.extras

        if (bundle != null) {
            val fakeLatitude = bundle.getSerializable("latitude")
            val fakeLongtitude = bundle.getSerializable("longitude")

            initiateNewUser(uid, fakeLatitude as Double, fakeLongtitude as Double) {
                if (!it) {
                    if (buttonVisible) {
                        v.tryAgainButton.revertAnimation()
                    }
                    changeAnimation("error")
                }
            }

        } else {
            getUserLocation { location ->
                latitude = location["latitude"]!!.toDouble()
                longitude = location["longitude"]!!.toDouble()
                Timber.v("Location %s", "Coordinates from phone : $location")
                if (latitude != 0.0 && longitude != 0.0) {
                    initiateNewUser(uid, latitude, longitude) {
                        if (!it) {
                            if (buttonVisible) {
                                v.tryAgainButton.revertAnimation()
                            }
                            changeAnimation("error")
                        }

                    }
                }

            }
        }


        v.tryAgainButton.setOnClickListener {
            if (latitude != 0.0 && longitude != 0.0) {
                changeAnimation("retry")
                tryAgain(latitude, longitude)
            }
        }


        return v;
    }



    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
    }

    private fun startUnavailableActivity() {
        val intent = Intent(activity, UnavailableActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun getUserLocation(myCallback: (result: Map<String, Double>) -> Unit) {

        locationService.getLocation { result ->
            myCallback.invoke(result)
        }

        activity?.let {
            FusedLocationService(it, this.requireActivity()).setUpLocationListener {
                Timber.tag("FusedLocation ").v(it.toString())
                myCallback.invoke(
                    mapOf<String, Double>(
                        "latitude" to it[0].latitude,
                        "longitude" to it[0].longitude
                    )
                )
            }
        }


    }


    fun initiateNewUser(
        uid: String,
        latitude: Double,
        longitude: Double,
        myCallback: (result: Boolean) -> Unit
    ) {

        GlobalScope.launch(Dispatchers.Main) {
            GenericApiService(uid, latitude, longitude).newUser {

                Timber.v("API %s", "Made api call ")

                if (it[0] == "true") {
                    Timber.v("API %s", "Response : ${it}")
                    FirebaseCrashlytics.getInstance().log("Api Response(New User) : ${it}")
                    if (it[1].equals("India")) {
                        startMainActivity()
                    } else {
                        startUnavailableActivity()

                    }

                } else {
                    myCallback.invoke(false)
                }
            }
        }
    }


    private fun tryAgain(latitude: Double, longitude: Double) {


        if (buttonVisible) {
            v.tryAgainButton.startAnimation()
            noOfRetry += 1
            Timber.v("API %s", "Retrying......  Retry Count$noOfRetry")
            FirebaseCrashlytics.getInstance()
                .log("Retrying...... lat : $longitude,  lon :  $latitude, Retry Count : $noOfRetry")
            initiateNewUser(uid, latitude, longitude) {
                if (!it) {
                    if (buttonVisible) {
                        v.tryAgainButton.revertAnimation()
                    }
                    changeAnimation("error")
                }

            }
        } else {
            v.tryAgainButton.isVisible = true
            buttonVisible = true
            tryAgain(latitude, longitude)
        }


    }


    fun changeAnimation(tag: String) {
        if (tag == "error") {
            v.animationView1.isVisible = false
            v.animationView2.isVisible = true
            v.tryAgainButton.isVisible = true
            v.statusTextServer.text = "Something Went Wrong"
        }
        if (tag == "retry") {

            v.animationView1.isVisible = true
            v.animationView2.isVisible = false
            v.tryAgainButton.isVisible = false
            v.statusTextServer.text = "Compiling your data. May Take few seconds."
        }
    }
}