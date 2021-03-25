package com.solvabit.climate.fragment.Dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.DashboardFragmentBinding
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class Dashboard : Fragment() {

    companion object {
        var localuser= User()
    }

    private var myUser = User()

    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dashboard_fragment, container, false
        )
        binding.myUser = myUser

        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val localRepo = Repository(dao,uid)
        if (localuser.uid == "1"){
            GlobalScope.launch(Dispatchers.Main){
                localRepo.getUser{
                    user -> localuser = user
                    Timber.i("LocalUser initiated")
                    Timber.i("User $user")
                    addDataToDashboard()
                }

            }
        }
        addDataToDashboard()

        return  binding.root
    }

    private fun addDataToDashboard() {
        myUser.normalizedScore = localuser.normalizedScore
        myUser.aqi = localuser.aqi
        myUser.forestDensity = localuser.forestDensity
        val normalizedScore: Float = myUser.normalizedScore?.toFloat() ?: 0f
        binding.circularProgressBar.progress = normalizedScore/10
        circularloader(myUser.aqi?.toFloat() ?: 0f, 500f, binding.circularProgressBarAirQuality)
        initializeAirData()
        initializeForestData()
    }

    private fun initializeAirData() {
        val co:Double = localuser.co ?: 0.toDouble()
        val so2:Double = localuser.so2 ?: 0.toDouble()
        val o3:Double = localuser.o3 ?: 0.toDouble()
        val no2: Double = localuser.no2 ?: 0.toDouble()
        val totalSum = co + so2 + o3 + no2
        val coPercentage = ( co.toFloat() /totalSum.toFloat() ) * 100
        val so2Percentage = ( so2.toFloat() /totalSum.toFloat() ) * 100
        val o3Percentage = ( o3.toFloat() /totalSum.toFloat() ) * 100
        val no2Percentage = ( no2.toFloat() / totalSum.toFloat()) * 100
        binding.apply {
            this.coAirQualityProgressView.progress = coPercentage
            this.so2AirQualityProgressbar.progress = so2Percentage
            this.o3AirQualityProgressView.progress = o3Percentage
            this.no2AirQualityProgressView.progress = no2Percentage
        }
    }

    private fun initializeForestData() {
        circularloader(myUser.forestDensity?.toFloat() ?: 0f, 10f, binding.circularProgressBarForestDensity)
        val actualForest:Int = localuser.actualForest ?: 0
        val openForest:Int = localuser.openForest ?: 0
        val noForest:Int = localuser.noForest ?: 0
        val totalSum = actualForest + openForest + noForest
        val actualForestPercentage = ( actualForest.toFloat() /totalSum.toFloat() ) * 100
        val openForestPercentage = ( openForest.toFloat() /totalSum.toFloat() ) * 100
        val noForestPercentage = ( noForest.toFloat() /totalSum.toFloat() ) * 100
        binding.apply {
            this.actualForestCoverProgressView.progress = actualForestPercentage
            this.openForestProgessView.progress = openForestPercentage
            this.noForestProgressView.progress = noForestPercentage
        }
    }

    private fun circularloader(data: Float, max:Float, circularProgressBar : CircularProgressBar){
        circularProgressBar.apply {
            setProgressWithAnimation(data, 3000) // =1s
            progressMax = max
            roundBorder = true
            startAngle = 180f
        }
    }

}