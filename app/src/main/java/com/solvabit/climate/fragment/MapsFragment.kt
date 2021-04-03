package com.solvabit.climate.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.PlantedTrees
import com.solvabit.climate.database.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_sheet_planted_tree_data.*
import kotlinx.android.synthetic.main.bottom_sheet_planted_tree_data.view.*
import kotlinx.android.synthetic.main.card_post_view.view.*
import kotlinx.android.synthetic.main.fragment_maps.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*


class MapsFragment : Fragment() {

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    @SuppressLint("MissingPermission")
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
        val IndiaBounds = LatLngBounds(
                LatLng((8.0), 68.7),  // SW bounds
                LatLng((37.0), 97.25) // NE bounds
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(IndiaBounds, 0))
        googleMap.setPadding(0, 0, 0, 0)
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.isMyLocationEnabled = true
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.style_json
                )
        )
        country_zoom.setOnClickListener {
            val country = LatLngBounds(
                    LatLng((8.0), 68.7),  // SW bounds
                    LatLng((37.0), 97.25) // NE bounds
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(country, 0))

        }
        state_zoom.setOnClickListener {
            val location = googleMap.myLocation
            if (location != null) {
                val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude)) // Sets the center of the map to location user
                        .zoom(7f) // Sets the zoom  // Sets the tilt of the camera to 30 degrees
                        .build() // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
        val ref = FirebaseDatabase.getInstance().getReference("/plantedTrees")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {


                val data = p0.getValue(PlantedTrees::class.java)


                val location = data?.lon?.let { LatLng(data.lat, it) }
                val marker = googleMap.addMarker(location?.let {
                    MarkerOptions().position(it).title("Rohit Kumar Planted tree on 02/03/21").snippet("Population: 4,137,400")
                            .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.marker))
                })
                marker.tag = p0.key


            }
        })


        googleMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            val position = marker.position
            val key = marker.tag


            /* val builder = AlertDialog.Builder(requireContext())
             builder.setTitle("Location of the tree planted")
             builder.setMessage("Position is $position")
             builder.setIcon(android.R.drawable.ic_dialog_map)
             val alertDialog: AlertDialog = builder.create()
             alertDialog.setCancelable(true)
             alertDialog.show() */

            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
            val bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_planted_tree_data, bottomsheet)


            val dataRef = FirebaseDatabase.getInstance().getReference("/plantedTrees/$key")
            dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(PlantedTrees::class.java)

                    bottomSheetView.findViewById<TextView>(R.id.tree_planted_user).text = data?.username
                    if (data?.user_image != null) Picasso.get().load(data.user_image).into(bottomSheetView.tree_planted_userImage)


                    if (data?.plant_image != null) Picasso.get().load(data.plant_image).into(bottomSheetView.plant_image)
                    bottomSheetView.findViewById<TextView>(R.id.location).text = position.toString()
                    if (data != null) {
                        val time = data.time.toLong()
                        val sfd = SimpleDateFormat("dd-MM-yyyy")
                        bottomSheetView.findViewById<TextView>(R.id.timing_of_tree_planted).text = sfd.format(Date(time))
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            bottomSheetView.dismiss_bottom_sheet.setOnClickListener {
                bottomSheetDialog.dismiss()
            }


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()



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