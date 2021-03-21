package com.solvabit.climate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    //private lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this,R.id.myNavHostFragment)
        bottomNavigation.setupWithNavController(navController)


    }


}