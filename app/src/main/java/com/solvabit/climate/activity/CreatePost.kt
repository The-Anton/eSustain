package com.solvabit.climate.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.database.User
import com.solvabit.climate.databinding.ActivityCreatePostBinding
import com.solvabit.climate.dialog.Dialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_post.*
import timber.log.Timber
import java.util.*

class CreatePost : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private var selectedPhotoUri : Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_post)
        
        fetchCurrentUser()

        val spinner: Spinner = binding.spinner2
        ArrayAdapter.createFromResource(
                this,
                R.array.planets_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        binding.addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        binding.postButton.setOnClickListener {
            val text = binding.postText.text.toString()
            if (binding.postText.text.isEmpty()){
                Toast.makeText(this,"Sorry, Blank can't be posted!",Toast.LENGTH_SHORT).show()
            }else{
                Dialog().show(supportFragmentManager, "MyCustomFragment")
                uploadImage()
            }
        }
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)
                binding.username.text = currentUser?.username
                if (currentUser?.imageUrl!=null){
                    Picasso.get().load(currentUser.imageUrl).into(user_image)
                }

            }

        })
    }

    private fun uploadImage() {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Timber.i( "Photo uploaded successfully: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener{
                        Timber.i( "image downloaded url : $it")
                        post(it.toString())
                    }
                }
    }

    private fun post(postImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val time = System.currentTimeMillis().toString()
        val max = Long.MAX_VALUE
        val timestamp = max - System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/PostData/$timestamp")
        val text = binding.postText.text.toString()
        val category = binding.spinner2.selectedItem.toString()
        val postData = Post(text,postImageUrl,uid,time,category,0)
        ref.setValue(postData).addOnSuccessListener {
            Toast.makeText(this,"Posted Successfully!!",Toast.LENGTH_SHORT).show()
            val intent  = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode== Activity.RESULT_OK && data!= null)
        {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            binding.postImage.setImageBitmap(bitmap)
            binding.postCard.updatePadding(bottom = 8)
        }

    }
}