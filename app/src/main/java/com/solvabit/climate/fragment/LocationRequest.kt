package com.solvabit.climate.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.solvabit.climate.R
import com.solvabit.climate.location.LocationService
import kotlinx.android.synthetic.main.fragment_location_request.view.*


class LocationRequest : Fragment() {

    lateinit var v: View
    lateinit var locationService: LocationService
    val mainHandler = Handler(Looper.getMainLooper())

    val runner = object : Runnable {
        override fun run() {
            if (locationService.locationEnabled()) {
                val fragment = ProcessLocation()
                val transaction = activity?.supportFragmentManager?.beginTransaction()

                if (transaction != null) {
                    transaction.replace(R.id.frameLocationFragment, fragment)
                    transaction.commit()
                }

            } else {
//                Toast.makeText(
//                        context,
//                        "Please turn on your location ",
//                        Toast.LENGTH_SHORT
//                ).show()
                mainHandler.postDelayed(this, 3000)
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_location_request, container, false)

        locationService = context?.let { LocationService(it.applicationContext) }!!

        mainHandler.post(runner)

        v.turnOnLocationButton.setOnClickListener {
            val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);

        }

        return v;
    }



}