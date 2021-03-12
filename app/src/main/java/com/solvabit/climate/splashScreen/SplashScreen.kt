package com.solvabit.climate.splashScreen

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.onboarding.Onboarding

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        Handler().postDelayed(
            {
                verifyUserIsLoggedIn()
            }, 2000)
    }

    private fun verifyUserIsLoggedIn(){

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        else
        {
            val intent = Intent(this, Onboarding::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Animatoo.animateFade(this)
        }
    }

}