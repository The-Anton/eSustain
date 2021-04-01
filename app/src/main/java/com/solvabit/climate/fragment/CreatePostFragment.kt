package com.solvabit.climate.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.databinding.FragmentCreatePostBinding
import com.solvabit.climate.dialog.Dialog
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.*

class CreatePostFragment : Fragment() {

    lateinit var binding: FragmentCreatePostBinding
    private val localUser = Dashboard.localuser
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_create_post, container, false
        )

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

        return binding.root
    }

    private fun createPost() {
        if (binding.postText.text.isEmpty()) {
            Toast.makeText(requireContext(), "Sorry, Blank can't be posted!", Toast.LENGTH_SHORT).show()
        } else {
            Timber.i("Going to show dialog box")
            Dialog().show(childFragmentManager, "MyCustomFragment")
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (selectedPhotoUri == null) {
            post("")
        } else {
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
        val postData = Post(text, postImageUrl, uid, time, category, timestamp.toString(), 0)
        ref.setValue(postData).addOnSuccessListener {
            Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
            binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
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