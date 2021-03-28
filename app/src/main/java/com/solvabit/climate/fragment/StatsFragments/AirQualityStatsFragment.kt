package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.DashboardFragmentBindingImpl
import com.solvabit.climate.databinding.FragmentAirQualityStatsBinding
import com.solvabit.climate.dialog.AqiDialog
import com.solvabit.climate.fragment.Dashboard

class AirQualityStatsFragment : Fragment() {

    lateinit var binding: FragmentAirQualityStatsBinding
    val localuser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_air_quality_stats, container, false
        )


        addData()

        binding.airQualityMoreStats.setOnClickListener {
            AqiDialog().show(childFragmentManager, "AQId")
        }

        return binding.root

    }

    private fun addData() {
        circularloader(localuser.aqi?.toFloat() ?: 0f, 500f, binding.circularProgressBarAirQualityStats)

        binding.apply {
            this.airQualityStats.text = localuser.aqi.toString()
            this.pm10Stats.text = localuser.pm10.toString()
            this.pm25Stats.text = localuser.pm25.toString()
            this.coAirQuality.text = localuser.co.toString()
            this.so2AirQuality.text = localuser.so2.toString()
            this.o3AirQuality.text = localuser.o3.toString()
            this.no2AirQuality.text = localuser.no2.toString()
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