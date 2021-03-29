package com.solvabit.climate.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.solvabit.climate.R
import com.solvabit.climate.location.Location
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_location.randomButton
import kotlinx.android.synthetic.main.activity_unavailable.*
import kotlin.random.Random

class UnavailableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unavailable)


        randomButton1.setOnClickListener{
            val location = generateRandomLocation()
            startLocationActivity(location)
        }

    }


    fun generateRandomLocation():List<Double>{
        var arr = arrayOf(doubleArrayOf(28.679079,77.069710), doubleArrayOf(28.7041,77.1025), doubleArrayOf(30.3752, 76.78213),
            doubleArrayOf(27.1767, 78.0081), doubleArrayOf(26.4499, 80.3319))
        val random = (0..5).random()
        Log.v("Unavailable",listOf(arr[random][0],arr[random][1]).toString())

        return listOf(arr[random][0],arr[random][1])
    }

    fun startLocationActivity(location: List<Double>){
        val intent = Intent(this, Location::class.java)
        val extras = Bundle()
        extras.putSerializable("latitude", location[0])
        extras.putSerializable("longitude", location[1])
        intent.putExtras(extras)
        Log.v("Intent", "Stared Intent2")

        startActivity(intent)
    }
}