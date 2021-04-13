package com.solvabit.climate.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.IssueDataMaps
import com.solvabit.climate.dataModel.PlantedTrees
import com.solvabit.climate.location.PERMISSION_REQUEST
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_planted_tree_data.*
import kotlinx.android.synthetic.main.bottom_sheet_planted_tree_data.view.*
import kotlinx.android.synthetic.main.fragment_maps.*
import java.text.SimpleDateFormat
import java.util.*


class MapsFragment : Fragment() {

    lateinit var locationManager: LocationManager
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var uid: String
    var gpsStatus: Boolean = false


    var mapMarkerKeys = mutableMapOf<String,String>()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeBottomNavigationState()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }else{
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            }
        }

    }

    fun changeBottomNavigationState(){

        val bottomNavMenu = activity?.bottomNavigation?.menu

        bottomNavMenu?.getItem(0)?.isEnabled = true
        bottomNavMenu?.getItem(1)?.isEnabled = true
        bottomNavMenu?.getItem(2)?.isEnabled = true
        bottomNavMenu?.getItem(3)?.isEnabled = false
        bottomNavMenu?.getItem(4)?.isEnabled = true

    }
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
         * In this case, we just add a markerbmp.bmp near Sydney, Australia.
         * If Google Play services is not installed on this device and okay are you okay on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) */
        val IndiaBounds = LatLngBounds(
            LatLng((8.0), 68.7),  // SW bounds here bounds
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
                LatLng((8.0), 68.7),  // SW bounds bounds here
                LatLng((37.0), 97.25) // NE bounds
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(country, 0))

        }
        state_zoom.setOnClickListener {
            val location = googleMap.myLocation
            if (location != null) {
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude)) // Sets the center of the map to location user
                    .zoom(7f) // Sets the zoom  // Sets the tilt of the camera of the camera to 30 degrees
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

                val plantingTime = data?.time?.toLong()
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateString = simpleDateFormat.format(plantingTime)

                val location = data?.lon?.let { LatLng(data.lat, it) }
                var marker = googleMap.addMarker(location?.let {
                    MarkerOptions().position(it).title("${data.username} Planted tree on ${dateString}").snippet("Lat- ${data.lat}, Lon- ${data.lon}")
                        .icon(activity?.let { it1 -> bitmapDescriptorFromVector(it1.applicationContext, R.drawable.plantmarkersmall) })
                })
                marker.tag = p0.key
                mapMarkerKeys[p0.key.toString()] = "plantation"
                Log.v("Maps", marker.tag.toString() +" " + mapMarkerKeys.get(p0.key.toString()))

            }
        })


        val issueRef = FirebaseDatabase.getInstance().getReference("/issues")
        issueRef.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {


                val data = p0.getValue(IssueDataMaps::class.java)

                val plantingTime = data?.time?.toLong()
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateString = simpleDateFormat.format(plantingTime)

                val location = data?.lon?.let { LatLng(data.lat, it) }
                var marker = googleMap.addMarker(location?.let {
                    MarkerOptions().position(it).title("${data.username} started the issue. ").snippet("Dated ${dateString}")
                        .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.issuemarkersmall))
                })
                marker.tag = p0.key
                mapMarkerKeys[p0.key.toString()] = "issue"
                Log.v("Maps", marker.tag.toString() +" " + mapMarkerKeys.get(p0.key.toString()))



            }
        })

        googleMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            val position = marker.position
            val key = marker.tag
            var dataRef = FirebaseDatabase.getInstance().getReference("/plantedTrees/$key")

            /* val builder = AlertDialog.Builder(requireContext())
             builder.setTitle("Location of the tree planted")
             builder.setMessage("Position is $position")
             builder.setIcon(android.R.drawable.ic_dialog_map)
             val alertDialog: AlertDialog = builder.create()
             builder.create()
             alertDialog.setCancelable(true)
             alertDialog.show()*/

            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.Theme_MaterialComponents_Light_BottomSheetDialog)
            val bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_planted_tree_data, bottomsheet)


            if(mapMarkerKeys[key] == "issue"){
                dataRef = FirebaseDatabase.getInstance().getReference("/issues/$key")
                dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.getValue(IssueDataMaps::class.java)

                        bottomSheetView.findViewById<TextView>(R.id.tree_planted_user).text = data?.username
                        bottomSheetView.findViewById<TextView>(R.id.bottomSheetTitle).text = "Issue started by user"


                        if (data?.user_image != null) Picasso.get().load(data.user_image).into(bottomSheetView.tree_planted_userImage)


                        if (data?.post_image != null) Picasso.get().load(data.post_image).into(bottomSheetView.plant_image)

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

            }

            if(mapMarkerKeys[key] == "plantation"){
                dataRef = FirebaseDatabase.getInstance().getReference("/plantedTrees/$key")
                dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.getValue(PlantedTrees::class.java)

                        bottomSheetView.findViewById<TextView>(R.id.tree_planted_user).text = data?.username
                        if (data?.user_image != null) Picasso.get().load(data.user_image).into(bottomSheetView.tree_planted_userImage)


                        if (data?.plant_image != null) Picasso.get().load(data.plant_image).into(bottomSheetView.plant_image)

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

            }


            val geoCoder = Geocoder(context, Locale.getDefault())

            val addresses = geoCoder.getFromLocation(position.latitude, position.longitude, 1)

            /*
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val zip = addresses[0].postalCode
            val zip = addresses[0].postalCode
            val country = addresses[0].countryName */
            if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                bottomSheetView.findViewById<TextView>(R.id.location).text = address
            }

            bottomSheetView.dismiss_bottom_sheet.setOnClickListener {
                bottomSheetDialog.dismiss()
            }


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()



            false
        })

    }


    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(requireContext(),permissionArray[i]) == PackageManager.PERMISSION_GRANTED)
                allSuccess = false
        }
        return allSuccess
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

            if(allSuccess){
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            }

        }
    }
}