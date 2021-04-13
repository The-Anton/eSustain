package com.solvabit.climate.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.IssueDataMaps
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.databinding.FragmentCreatePostBinding
import com.solvabit.climate.dialog.Dialog
import com.solvabit.climate.location.LocationService
import com.solvabit.climate.location.PERMISSION_REQUEST
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_create_post.*
import timber.log.Timber
import java.util.*


class CreatePostFragment : Fragment() {

    lateinit var binding: FragmentCreatePostBinding
    private val localUser = Dashboard.localuser
    private var selectedPhotoUri: Uri? = null
    lateinit var locationManager: LocationManager
    private var permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val mainHandler = Handler(Looper.getMainLooper())

    var locationCheckboxRunner = object : Runnable {
        override fun run() {
            if (!locationEnabled() && binding.checkShareLocation.isChecked) {
                Toast.makeText(
                        activity,
                        "Please turn on your location ",
                        Toast.LENGTH_SHORT
                ).show()
                binding.checkShareLocation.isChecked = false
            } else {

                mainHandler.postDelayed(this, 3000)
            }


        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_create_post, container, false
        )

        val args = CreatePostFragmentArgs.fromBundle(requireArguments())
        var isRecyclePost = args.recyclepost

        getUser()
        addSpinner()

        binding.backArrowCreatePost.setOnClickListener {
            binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
        }

        binding.addImage.setOnClickListener {
            addImage()
        }

        binding.postButton.setOnClickListener {
            Timber.i("Clicked on postImage")
            createPost()
        }


        val spnLocale = binding.spinner2 as Spinner

        if(isRecyclePost){
            spnLocale.setSelection(1)
            spnLocale.isEnabled = false
        }
        spnLocale.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (adapterView != null) {
                    if (adapterView.getItemAtPosition(i) == "Issue") {
                        binding.groupConditions.visibility=View.VISIBLE
                        binding.checkCreateGroup.visibility = View.VISIBLE
                        binding.checkShareLocation.visibility = View.VISIBLE
                    }
                    if (adapterView.getItemAtPosition(i) == "General" || adapterView.getItemAtPosition(i) == "Achievement") {
                        binding.groupConditions.visibility = View.GONE
                    }

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        }

        binding.checkCreateGroup.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.groupName.visibility = View.VISIBLE

            } else {
                binding.groupName.visibility = View.GONE
            }
        }

        binding.checkShareLocation.setOnCheckedChangeListener { buttonView,isChecked ->
            if(isChecked){
                checkLocationPermission()
                runnableHandler()
            }

        }


        return binding.root
    }


    private fun createPost() {
        if (binding.postText.text.isEmpty()) {
            Toast.makeText(requireContext(), "Sorry, Post can't be blank!", Toast.LENGTH_SHORT).show()
        } else {
            Timber.i("Going to show dialog box")

            uploadImage()
        }
    }

    private fun uploadImage() {
        if (selectedPhotoUri == null) {
            post("")
        } else {
            Dialog( ).show(childFragmentManager, "MyCustomFragment")
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
                        Timber.i("Photo uploaded successfully: ${it.metadata?.path}")

                        ref.downloadUrl.addOnSuccessListener {
                            Timber.i("image downloaded url : $it")
                            post(it.toString())
                        }
                    }
        }
    }

    private fun post(postImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val time = System.currentTimeMillis().toString()
        val max = Long.MAX_VALUE
        val timestamp = max - System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/PostData/$timestamp")
        val text = binding.postText.text.toString()
        val category = binding.spinner2.selectedItem.toString()
        if (category == "Issue") {
            saveIssueMapNode(postImageUrl)
            if (binding.checkCreateGroup.isChecked) {

                if (binding.groupName.text.isNotEmpty()) {

                    val groupName = binding.groupName.text.toString()
                    val postData = Post(text, postImageUrl, uid, time, category, timestamp.toString(), groupName, 0)
                    ref.setValue(postData).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
                        binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())

                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter Group Name!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val postData = Post(text, postImageUrl, uid, time, category, timestamp.toString(), "", 0)
                ref.setValue(postData).addOnSuccessListener {
                    mainHandler.removeCallbacks(locationCheckboxRunner)
                    Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
                    binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
                }
            }

        } else {

            val postData = Post(text, postImageUrl, uid, time, category, timestamp.toString(), "", 0)
            ref.setValue(postData).addOnSuccessListener {
                mainHandler.removeCallbacks(locationCheckboxRunner)
                Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
                binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())

            }
        }
    }


    fun runnableHandler() {


        mainHandler.post(locationCheckboxRunner)

    }


    fun  checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }else{
                askForLocation()
            }
        }
    }

    private fun askForLocation() {
        if(!locationEnabled()){
            Toast.makeText(context, "Please turn on your location", Toast.LENGTH_SHORT).show()
            binding.checkShareLocation.isChecked = false
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
                askForLocation()
            }

        }
    }



    private fun saveIssueMapNode(post_image: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val time = System.currentTimeMillis().toString()
        val max = Long.MAX_VALUE
        val timestamp = max - System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/issues/$timestamp")
        val text = binding.postText.text.toString()
        val category = binding.spinner2.selectedItem.toString()
        val groupName = binding.groupName.text.toString()


        activity?.let {val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            LocationService(uid,locationManager).getLocation(it.applicationContext) {
                val issueData = it["latitude"]?.let { it1 -> it["longitude"]?.let { it2 -> localUser.username?.let { it3 -> localUser.imageUrl?.let { it4 -> IssueDataMaps(it1,it2,time,uid,category,groupName," ",post_image,text, it3, it4) } } } }


                ref.setValue(issueData).addOnSuccessListener {
                    Timber.i("Location Saved!!")
                }
            }
        }



    }

    private fun addImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private fun addSpinner() {
        val spinner: Spinner = binding.spinner2
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.planets_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun getUser() {
        binding.username.text = localUser.username
        Picasso.get().load(localUser.imageUrl).into(binding.userImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            binding.postImage.setImageURI(selectedPhotoUri)
            binding.postCard.updatePadding(bottom = 8)
        }

    }

}
/*
class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
    /* val spinner: Spinner = findViewById(R.id.spinner2)
    spinner.onItemSelectedListener = this */
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)


    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
} */