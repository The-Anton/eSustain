package com.solvabit.climate.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.solvabit.climate.R
import com.solvabit.climate.location.Location
import kotlinx.android.synthetic.main.activity_unavailable.*
import timber.log.Timber

class UnavailableActivity : AppCompatActivity() {

    companion object {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unavailable)


        randomButton1.setOnClickListener {
            val location = generateRandomLocation()
            startLocationActivity(location)
        }

    }


    fun generateRandomLocation(): List<Double> {
        val arr = arrayOf(
            doubleArrayOf(28.679079, 77.069710),
            doubleArrayOf(28.7041, 77.1025),
            doubleArrayOf(30.3752, 76.78213),
            doubleArrayOf(27.1767, 78.0081),
            doubleArrayOf(26.4499, 80.3319)
        )
        val random = (0..4).random()
        Timber.v(
            "Unavailable For This Location: Random Location - %s",
            listOf(arr[random][0], arr[random][1]).toString()
        )
        firebaseCrashlytics.log(
            "Foreign User : Random location provided - " + listOf(
                arr[random][0],
                arr[random][1]
            ).toString()
        )
        return listOf(arr[random][0], arr[random][1])
    }

    private fun startLocationActivity(location: List<Double>) {
        val intent = Intent(this, Location::class.java)
        val extras = Bundle()
        extras.putSerializable("latitude", location[0])
        extras.putSerializable("longitude", location[1])
        intent.putExtras(extras)
        Timber.v("Intent - %s", "Started Location Activity")
        startActivity(intent)
    }
}