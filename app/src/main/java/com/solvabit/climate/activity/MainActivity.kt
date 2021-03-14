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
//




//        GlobalScope.launch{
//            // val db = Room.databaseBuilder(applicationContext, UserDatabase::class.java, "userDatabase" ).build()
//            val instance = UserDatabase.getInstance(this@MainActivity)
//            val dao = instance.userDao()
//
//            val repository = Repository(dao,"PdZV87UWm9QMi4KeR9n4gMiQ23f1",30.31,70.2,"Haryana")
//
//            /// userDao.insert(user)
//            val users = repository.getUser()
//
//            Log.v("User", dao.hasUser("PdZV87UWm9QMi4KeR9n4gMiQ23f1").toString())
//            //Log.v("User", users.toString())
//
//
//        }
//






    }


}