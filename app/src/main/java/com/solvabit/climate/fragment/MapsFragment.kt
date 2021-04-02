package com.solvabit.climate.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.TokenWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.PlantedTrees


class MapsFragment : Fragment() {

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) */
        val australiaBounds = LatLngBounds(
            LatLng((8.0), 68.7),  // SW bounds
            LatLng((37.0), 97.25) // NE bounds
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
        googleMap.uiSettings.isZoomControlsEnabled=true
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_json
            )
        )
        val ref = FirebaseDatabase.getInstance().getReference("/plantedTrees")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val data = p0.getValue(PlantedTrees::class.java)
                val location = data?.lon?.let { LatLng(data.lat, it) }
                googleMap.addMarker(location?.let {
                    MarkerOptions().position(it).title("Rohit Kumar Planted tree on 02/03/21").snippet("Population: 4,137,400")
                        .icon(bitmapDescriptorFromVector(requireContext(),R.drawable.marker))
                })
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
        })

        googleMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            val position = marker.position
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Location of the tree planted")
            builder.setMessage("Position is $position")
            builder.setIcon(android.R.drawable.ic_dialog_map)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()
            false
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}