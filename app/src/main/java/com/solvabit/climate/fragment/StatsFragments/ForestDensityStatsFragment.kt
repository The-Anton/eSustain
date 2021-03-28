package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentAirQualityStatsBinding
import com.solvabit.climate.databinding.FragmentForestDensityStatsBinding
import com.solvabit.climate.fragment.Dashboard

class ForestDensityStatsFragment : Fragment() {

    lateinit var binding: FragmentForestDensityStatsBinding
    val localuser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_forest_density_stats, container, false)

        addData()

        return binding.root
    }

    private fun addData() {
        circularloader(localuser.forestDensity?.toFloat()
                ?: 0f, 10f, binding.circularProgressBarForestQualityStats)
        binding.apply {
            this.forestQualityStats.text = localuser.forestDensity.toString()
            this.treesPlantedStats.text = localuser.treesPlanted.toString()
            this.treesReferredStats.text = localuser.treesReferred.toString()
            this.totalAreaForestStats.text = localuser.totalArea.toString()
            this.actualForestStats.text = localuser.actualForest.toString()
            this.openForestStats.text = localuser.openForest.toString()
            this.noForestStats.text = localuser.noForest.toString()
        }
    }

    private fun circularloader(data: Float, max: Float, circularProgressBar: CircularProgressBar) {
        circularProgressBar.apply {
            setProgressWithAnimation(data, 3000) // =1s
            progressMax = max
            roundBorder = true
            startAngle = 180f
        }
    }


}