package com.solvabit.climate.registerLogin

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
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
import com.solvabit.climate.database.UserDatabase
import kotlinx.android.synthetic.main.activity_registerpage.*
import java.util.*

class RegistrationPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerpage)
        val registerbtn = findViewById<CircularProgressButton>(R.id.register_button_register)

        val alreadyhaveaccount = findViewById<TextView>(R.id.already_account_register)

        //registration button click
        registerbtn.setOnClickListener{
            registerbtn.startAnimation()
            firebaseAuthwithEmailPassword()
        }

        facebook_register.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()
        }

        apple_register.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()
        }

        google_register.setOnClickListener{

            registerbtn.startAnimation()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            auth = FirebaseAuth.getInstance()

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        //already have an account click listener
        already_account_register.setOnClickListener{
            val myintent = Intent(this, loginpage::class.java)
            startActivity(myintent)
        }

        //image selection click listener
        image_button_register.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }


    var selectedPhotoUri : Uri?= null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode== Activity.RESULT_OK && data!= null)
        {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            imageview_register.setImageBitmap(bitmap)
            image_button_register.alpha = 0f
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }

    }


    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    val act: GoogleSignInAccount?= GoogleSignIn.getLastSignedInAccount(this)
                    selectedPhotoUri = act?.photoUrl
                    val username = act?.displayName
                    val email = act?.email
                    saveUserToFirebaseDatabase(username!!, email!!, selectedPhotoUri.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Sign in failed!! ", Toast.LENGTH_SHORT).show()
                    register_button_register.revertAnimation()
                }
            }
    }


    private fun firebaseAuthwithEmailPassword(){

        val email = email_editText_register.text.toString()
        val password = password_editText_register.text.toString()
        val username = username_editText_register.text.toString()

        Log.e("register","Email: $email and password: $password and username: $username")

        if(email.isEmpty() || password.isEmpty()){
            register_button_register.revertAnimation()
            Toast.makeText(this , "Enter all fields first ", Toast.LENGTH_SHORT).show()
            return
        }
        var auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    register_button_register.revertAnimation()
                    return@addOnCompleteListener
                }
                else{

                    uploadPhotoToFirebase(username, email)
                }
            }
            .addOnFailureListener{
                register_button_register.revertAnimation()
                Toast.makeText(this, "Error creating account. ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhotoToFirebase(username: String, email: String) {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.e("registerActivity", "Photo uploaded successfully: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.e("registerActivity", "image downloaded url : $it")
                    saveUserToFirebaseDatabase(username, email, it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(username: String, email: String, profileImageUrl : String){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val status = "Not set"
        val active = true
        var users = User(
            active,
                0,
                0.0,
                false,
                "City",
                0.0,
                listOf("0"),
                "Country",
                email,
                0.0,
                profileImageUrl,
                listOf("0.0","0.0"),
                false,
                0.0,
                0,
                0.0,
                0.0,
                listOf("0"),
                0,
                0.0,
                0.0,
                listOf("0"),
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
            username
        )
        ref.setValue(users)
            .addOnSuccessListener {
                Log.e("registerActivity","User is saved with $uid and $profileImageUrl")
//                val instance = UserDatabase.getInstance(this)
//                val dao = instance.userDao()
//                dao.insert(users)
//                Log.v("registerActivity", "User is saved in room with  ${dao.getUserByUID(uid)}")

                val user = FirebaseAuth.getInstance().currentUser

                user!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG,"Email sent")
                        }
                    }

                val deepColor = Color.parseColor("#27E1EF")
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
                register_button_register.doneLoadingAnimation(deepColor, largeIcon)
                val handler = Handler()
                handler.postDelayed({
                    Toast.makeText(this, "Account created successfully. Email sent to confirm.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                },1000)
            }
    }

    private fun verifyUserIsLoggedIn(){

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "Register_Activity"
        private const val RC_SIGN_IN = 9001
    }
}

//
//@Parcelize
//class Users(val uid: String , val username: String , val email: String,
//            val imageUrl : String, val status:String, val active: Boolean,
//            val timestamp: Long, val treesPlanted: Int): Parcelable {
//    constructor(): this("","","","","",true,-1, 0)
//}
