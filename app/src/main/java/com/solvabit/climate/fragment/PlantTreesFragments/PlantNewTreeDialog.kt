package com.solvabit.climate.fragment.PlantTreesFragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.PlantedTrees
import com.solvabit.climate.fragment.Dashboard
import com.solvabit.climate.fragment.Trees
import com.solvabit.climate.location.LocationService
import com.solvabit.climate.location.PERMISSION_REQUEST
import kotlinx.android.synthetic.main.dialog_plant_new_tree.view.*
import timber.log.Timber
import java.util.*

class PlantNewTreeDialog(
    private val targetTrees: Int,
    private var treesPlanted: Int,
    private var taskId: String
) : DialogFragment() {

    private lateinit var dialogView: View
    private val uid = FirebaseAuth.getInstance().uid
    private val localUser = Dashboard.localuser
    lateinit var locationManager: LocationManager
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val mainHandler = Handler(Looper.getMainLooper())


    var locationCheckboxRunner = object : Runnable {
        override fun run() {
            if (!locationEnabled() && dialogView.checkbox_plant_tree_dialog.isChecked) {
                Toast.makeText(
                    activity,
                    "Please turn on your location ",
                    Toast.LENGTH_SHORT
                ).show()
                dialogView.checkbox_plant_tree_dialog.isChecked = false
            } else {

                mainHandler.postDelayed(this, 3000)
            }


        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_plant_new_tree, null)
        builder.setView(dialogView)


        dialogView.add_pic_trees_button_dialog.setOnClickListener {
            uploadImageDataToDevice()

        }


        return builder.create()
    }


    private fun uploadImageDataToDevice() {
        dialogView.add_pic_trees_button_dialog.startAnimation()
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }

    private var selectedPhotoUri: Uri? = null

    private var imageUrl: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val deepColor = Color.parseColor("#27E1EF")
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
            dialogView.add_pic_trees_button_dialog.doneLoadingAnimation(deepColor, largeIcon)
            dialogView.grow_tree_animation_dialog.visibility = View.GONE
            dialogView.add_pic_trees_button_dialog.visibility = View.GONE
            dialogView.imageView_tree_dialog.setImageURI(selectedPhotoUri)
            dialogView.imageView_tree_dialog.visibility = View.VISIBLE
            dialogView.post_image_button_dialog.visibility = View.VISIBLE
            dialogView.checkbox_plant_tree_dialog.visibility = View.VISIBLE
            dialogView.checkbox_plant_tree_dialog.setOnCheckedChangeListener { buttonView, isChecked ->
                runnableHandler()
                checkLocationPermission()
            }
            dialogView.post_image_button_dialog.setOnClickListener {
                dialogView.post_image_button_dialog.startAnimation()
                uploadPhotoToFirebase()
            }


        } else {
            dialogView.add_pic_trees_button_dialog.revertAnimation()
            return
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainHandler.removeCallbacks(locationCheckboxRunner)

    }

    override fun onDetach() {
        super.onDetach()
        mainHandler.removeCallbacks(locationCheckboxRunner)

    }

    private fun uploadPhotoToFirebase() {
        if (selectedPhotoUri == null) {
            dialogView.post_image_button_dialog.revertAnimation()
            dialogView.post_image_button_dialog.visibility = View.INVISIBLE
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance()
            .getReference("/images-send-by-users-for-planting-tree/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    imageUrl = it.toString()
                    val timestamp = System.currentTimeMillis()
                    val dataRef = FirebaseDatabase.getInstance()
                        .getReference("/Users/$uid/AllTasks/$taskId/$timestamp")
                    val tree = Trees(
                        imageUrl.toString(),
                        System.currentTimeMillis()
                    )

                    dataRef.setValue(tree)
                        .addOnSuccessListener {
                            if (dialogView.checkbox_plant_tree_dialog.isChecked) {
                                shareOnMaps(imageUrl!!, timestamp)
                            }

                            FirebaseDatabase.getInstance().getReference("/Users/$uid/treesPlanted")
                                .get()
                                .addOnSuccessListener {
                                    treesPlanted = it.value.toString().toInt() + 1
                                    FirebaseDatabase.getInstance().getReference("/Users/$uid")
                                        .child("treesPlanted").setValue(treesPlanted)
                                        .addOnSuccessListener {
                                            mainHandler.removeCallbacks(locationCheckboxRunner)
                                            dialog?.dismiss()
                                        }
                                }
                            selectedPhotoUri = null

                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Unable to add Image!!\n Check your connection",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialogView.add_pic_trees_button_dialog.revertAnimation()
                            dialogView.post_image_button_dialog.revertAnimation()
                            dialogView.post_image_button_dialog.visibility = View.INVISIBLE
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Unable to add Image!!\n Check your connection",
                    Toast.LENGTH_SHORT
                ).show()
                dialogView.add_pic_trees_button_dialog.revertAnimation()
                dialogView.post_image_button_dialog.revertAnimation()
                dialogView.post_image_button_dialog.visibility = View.INVISIBLE
            }
    }

    private fun shareOnMaps(imageUrl: String, timestamp: Long) {
        Timber.i("Inside share on maps")

        activity?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            uid?.let { it1 ->
                LocationService(it1, locationManager).getLocation(it.applicationContext) {
                    val plantTreeOnMaps = it["latitude"]?.let { it2 ->
                        it["longitude"]?.let { it3 ->
                            PlantedTrees(
                                it2,
                                it3,
                                imageUrl,
                                timestamp.toString(),
                                uid,
                                localUser.username!!,
                                localUser.imageUrl!!
                            )
                        }
                    }
                    FirebaseDatabase.getInstance()
                        .getReference("/plantedTrees").push().setValue(plantTreeOnMaps)
                        .addOnSuccessListener {
                            Timber.i("Planted Tree on map")
                        }
                }
            }
        }


    }


    fun runnableHandler() {


        mainHandler.post(locationCheckboxRunner)

    }


    fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            } else {
                askForLocation()
            }
        }
    }

    private fun askForLocation() {
        if (!locationEnabled()) {
            Toast.makeText(context, "Please turn on your location", Toast.LENGTH_SHORT).show()
            dialogView.checkbox_plant_tree_dialog.isChecked = false
        }
    }


    private fun locationEnabled(): Boolean {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return gpsStatus
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permissionArray[i]
                ) == PackageManager.PERMISSION_GRANTED
            )
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

            if (allSuccess) {
                askForLocation()
            }

        }
    }


}