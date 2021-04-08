package com.solvabit.climate.registerLogin

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.database.User
import com.solvabit.climate.databinding.ActivityRegisterpageBinding
import timber.log.Timber
import java.util.*

class RegistrationPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterpageBinding
    private var selectedPhotoUri : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registerpage)

        Timber.i("Called ViewModelProvider")
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        //registration button click
        binding.registerButtonRegister.setOnClickListener{
            binding.registerButtonRegister.startAnimation()
            firebaseAuthwithEmailPassword()
        }

        //google button click
        binding.googleRegister.setOnClickListener{
            startGoogleAuth()
        }

        //facebook button click
        binding.facebookRegister.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()
        }

        //apple button click
        binding.appleRegister.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()
        }


        //already have an account click listener
        binding.alreadyAccountRegister.setOnClickListener{
            val myintent = Intent(this, loginpage::class.java)
            startActivity(myintent)
        }

        //image selection click listener
        binding.imageButtonRegister.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode== Activity.RESULT_OK && data!= null)
        {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            binding.imageviewRegister.setImageBitmap(bitmap)
            binding.imageButtonRegister.alpha = 0f
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.i("Google sign in failed $e")
            }
        }

    }


    private fun startGoogleAuth(){
        binding.registerButtonRegister.startAnimation()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val act: GoogleSignInAccount?= GoogleSignIn.getLastSignedInAccount(this)
                    selectedPhotoUri = act?.photoUrl
                    val username = act?.displayName
                    val email = act?.email
                    Timber.i("Auth with google successful")
                    saveUserToFirebaseDatabase(username!!, email!!, selectedPhotoUri.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this,"Sign in failed!! ", Toast.LENGTH_SHORT).show()
                    binding.registerButtonRegister.revertAnimation()
                }
            }
    }


    private fun firebaseAuthwithEmailPassword(){

        val email = binding.emailEditTextRegister.text.toString()
        val password = binding.passwordEditTextRegister.text.toString()
        val username = binding.usernameEditTextRegister.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            binding.registerButtonRegister.revertAnimation()
            Toast.makeText(this , "Enter all fields first ", Toast.LENGTH_SHORT).show()
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    binding.registerButtonRegister.revertAnimation()
                    return@addOnCompleteListener
                }
                else{

                    uploadPhotoToFirebase(username, email)
                }
            }
            .addOnFailureListener{
                binding.registerButtonRegister.revertAnimation()
                Toast.makeText(this, "Error creating account. ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun uploadPhotoToFirebase(username: String, email: String) {
        if(selectedPhotoUri == null){
            val defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/forest-59f3b.appspot.com/o/images%2Fprofile_picture.jpg?alt=media&token=d56efeb6-bc80-4acc-836e-96032c712be7"
            saveUserToFirebaseDatabase(username, email, defaultProfilePic)
        }
        else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.e("registerActivity", "Photo uploaded successfully: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.e("registerActivity", "image downloaded url : $it")
                        saveUserToFirebaseDatabase(username, email, it.toString())
                    }
                }
        }
    }


    private fun saveUserToFirebaseDatabase(username: String, email: String, profileImageUrl : String){
        Timber.i("save User to Firebase")
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val status = "Not set"
        val active = true
        val users = User(
            active,
                0,
                0.0,
                false,
                "City",
                0.0,
                listOf(),
                "Country",
                email,
                0.0,
                listOf("0"),
                profileImageUrl,
                listOf("0.0","0.0"),
                false,
                0.0,
                0,
                0.0,
                0.0,
<<<<<<< HEAD
                listOf("0", "1"),
=======
                listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"),
>>>>>>> 27206dfae73fdc3497e45e1040d5fa1ce89931d7
                0,
                0.0,
                0.0,
                listOf("1"),
            "Rookie",
                0,
                0.0,
                "State",
            status,
                listOf("0"),
            System.currentTimeMillis(),
            0,
            0,
                0,
            uid,
            false,
            username,
                mapOf()
        )
        ref.setValue(users)
            .addOnSuccessListener {
                Timber.i("User is saved with $uid and $profileImageUrl")
                val user = FirebaseAuth.getInstance().currentUser

                user!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Timber.i("Email sent")
                        }
                    }

                Timber.i("User saved to firebase")
                val deepColor = Color.parseColor("#27E1EF")
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
                binding.registerButtonRegister.doneLoadingAnimation(deepColor, largeIcon)
                Handler().postDelayed({
                    Toast.makeText(this, "Account created successfully. Email sent to confirm.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                },1000)
            }
    }


    companion object {
        private const val RC_SIGN_IN = 9001
    }
}