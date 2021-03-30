package com.solvabit.climate.splashScreen

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.activity.MainActivity
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.onboarding.Onboarding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Timber.plant(Timber.DebugTree())

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        Handler().postDelayed(
            {
                verifyUserIsLoggedIn(this)
            }, 1250)
    }

    private fun verifyUserIsLoggedIn(context: Context){

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
                val uid = FirebaseAuth.getInstance().uid.toString()


                GlobalScope.launch(Dispatchers.Main){
                    // val db = Room.databaseBuilder(applicationContext, UserDatabase::class.java, "userDatabase" ).build()
                    val instance = UserDatabase.getInstance(context)
                    val dao = instance.userDao()
                    val repository = Repository(dao,uid)
                    val noOfUser =dao.hasUser(uid)
                    Log.v("User", noOfUser.toString())

                    if(noOfUser==1){
                        startMainActivity()
                    }else{
                        startLocationActivity()
                    }
                }

        }
        else
        {
            val intent = Intent(this, Onboarding::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Animatoo.animateFade(this)
        }
    }


    fun  startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Animatoo.animateFade(this)
    }

    fun  startLocationActivity(){
        val intent = Intent(this, com.solvabit.climate.location.Location::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or ( Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Animatoo.animateFade(this)
    }
}