package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentForestDensityStatsBinding
import com.solvabit.climate.databinding.FragmentGroundWaterStatsBinding
import com.solvabit.climate.fragment.Dashboard
import kotlinx.android.synthetic.main.fragment_ground_water_stats.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class GroundWaterStatsFragment : Fragment() {
    lateinit var binding: FragmentGroundWaterStatsBinding
    val localuser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_ground_water_stats, container, false
        )

        addData()

        return binding.root
    }

    private fun addData() {
        circularloader(
            localuser.groundWaterData[11]?.toFloat()
                ?: 0f, 300f, binding.circularProgressBarGroundWaterStageStats
        )


        binding.apply {
            this.groundwaterstage.text = localuser.groundWaterData[11].toString()
            this.annualDomesticAllocationBy2025.text = localuser.groundWaterData[0].toString()
            this.currentAllocationForUse.text = localuser.groundWaterData[3].toString()
            this.totalAnnualRecharge.text = localuser.groundWaterData[5].toString()
            this.currentAnnualExtraction.text = localuser.groundWaterData[2].toString()
            this.totalNaturalDischarge.text = localuser.groundWaterData[12].toString()
            this.netAvaibilityForFuture.text = localuser.groundWaterData[6].toString()
            this.rainfallMonsoonRecharge.text = localuser.groundWaterData[9].toString()
            this.rainfallNonMonsoonRecharge.text = localuser.groundWaterData[10].toString()

        }




    }



    fun circularloader(data: Float, max: Float, circularProgressBar: CircularProgressBar) {
        circularProgressBar.apply {
            setProgressWithAnimation(data, 3000) // =1s
            progressMax = max
            roundBorder = true
            startAngle = 180f
        }
    }
}