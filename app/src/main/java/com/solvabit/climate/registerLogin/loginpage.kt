package com.solvabit.climate.registerLogin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.databinding.ActivityLoginpageBinding
import timber.log.Timber

class loginpage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginpageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loginpage)

        //Login function
        login()

        //forgot password
        binding.forgotPasswordLogin.setOnClickListener{
            val intent = Intent(this, ResetPassword::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        //Back to registration text
        binding.backToRegistrationLogin.setOnClickListener{
            val intent = Intent(this , RegistrationPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun login(){

        binding.imageView2.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()

        }

        binding.imageView3.setOnClickListener {
            Toast.makeText(this,"Support Not Availaible!! ", Toast.LENGTH_SHORT).show()
        }

        binding.googleLogin.setOnClickListener {

            binding.loginButtonLogin.startAnimation()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            auth = FirebaseAuth.getInstance()

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        binding.loginButtonLogin.setOnClickListener{

            binding.loginButtonLogin.startAnimation()

            val email = binding.emailEditTextLogin.text.toString()
            val password = binding.passwordEditTextLogin.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                binding.loginButtonLogin.revertAnimation()
                Toast.makeText(this, "Enter all Credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(it.isSuccessful) {
                        val deepColor = Color.parseColor("#27E1EF")
                        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
                        binding.loginButtonLogin.doneLoadingAnimation(deepColor, largeIcon)
                        val uid = FirebaseAuth.getInstance().uid
                        FirebaseDatabase.getInstance().getReference("/Users/$uid/active")
                            .setValue(true)
                        val handler = Handler()
                        handler.postDelayed({
                            Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        },1000)
                    }
                    else
                    {
                        binding.loginButtonLogin.revertAnimation()
                        Toast.makeText(this, "Wrong credentials. Try again!!", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
                .addOnFailureListener {
                    binding.loginButtonLogin.revertAnimation()
                    Toast.makeText(this , "Error Logging in !!", Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.i( "firebaseAuthWithGoogle: $account.id")

                Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()

                val deepColor = Color.parseColor("#27E1EF")
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
                binding.loginButtonLogin.doneLoadingAnimation(deepColor, largeIcon)
                val uid = FirebaseAuth.getInstance().uid
                FirebaseDatabase.getInstance().getReference("/Users/$uid/active")
                    .setValue(true)
                val handler = Handler()
                handler.postDelayed({
                    Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                },1000)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.i("Google sign in failed: $e")
                Toast.makeText(this, "Failed to Sign in !!", Toast.LENGTH_SHORT).show()
                binding.loginButtonLogin.revertAnimation()
            }
        }

    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}
