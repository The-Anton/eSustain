package com.solvabit.climate.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.IssueOnMaps
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.databinding.FragmentCreatePostBinding
import com.solvabit.climate.dialog.Dialog
import com.solvabit.climate.fragment.Tasks.GuideTaskArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_create_post.*
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


        return binding.root
    }


    private fun createPost() {
        if (binding.postText.text.isEmpty()) {
            Toast.makeText(requireContext(), "Sorry, Blank can't be posted!", Toast.LENGTH_SHORT).show()
        } else {
            Timber.i("Going to show dialog box")

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
        if (category == "Issue") {
            saveUserLocation(postImageUrl)
            if (binding.checkCreateGroup.isChecked) {

                if (binding.groupName.text.isNotEmpty()) {

                    Dialog().show(childFragmentManager, "MyCustomFragment")
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
                    Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
                    binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
                }
            }

        } else {

            Dialog().show(childFragmentManager, "MyCustomFragment")
            val postData = Post(text, postImageUrl, uid, time, category, timestamp.toString(), "", 0)
            ref.setValue(postData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
                binding.root.findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())

            }
        }
    }

    private fun saveUserLocation(post_image: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val time = System.currentTimeMillis().toString()
        val max = Long.MAX_VALUE
        val timestamp = max - System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/issues/$timestamp")
        val text = binding.postText.text.toString()
        val category = binding.spinner2.selectedItem.toString()
        val groupName = binding.groupName.text.toString()
        val issueData = IssueOnMaps(category,groupName,0.0,0.0,post_image,text,time,uid)
        ref.setValue(issueData).addOnSuccessListener {
            Timber.i("Location Saved!!")
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