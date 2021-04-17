package com.solvabit.climate.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.solvabit.climate.R
import com.solvabit.climate.location.LocationService
import com.solvabit.climate.location.PERMISSION_REQUEST
import kotlinx.android.synthetic.main.fragment_location_permission_info.view.*
import timber.log.Timber

class LocationPermissionInfo : Fragment() {

    lateinit var v: View
    lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_location_permission_info, container, false)

        locationService = activity?.let { LocationService(it.applicationContext) }!!

        v.grantPermissionButton.setOnClickListener {
            if (locationService != null) {
                requestPermissions(locationService.permissions, PERMISSION_REQUEST)
            }
        }
        return v;
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
                }
            }
            if (allSuccess) {
                Timber.tag("Permission").v("Got permission")

                if (locationService.locationEnabled()) {
                    val fragment = ProcessLocation()

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    if (transaction != null) {
                        transaction.replace(R.id.frameLocationFragment, fragment)
                        transaction.commit()
                    }
                } else {
                    val fragment = LocationRequest()

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    if (transaction != null) {
                        transaction.replace(R.id.frameLocationFragment, fragment)
                        transaction.commit()
                    }
                }


            }

        }
    }
}