package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentGroundWaterStatsBinding
import com.solvabit.climate.fragment.Dashboard
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class GroundWaterStatsFragment : Fragment() {
    lateinit var binding: FragmentGroundWaterStatsBinding
    val localuser = Dashboard.localuser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_ground_water_stats, container, false
        )

        addData()
        setPieChart()
        return binding.root
    }


    private fun setPieChart() {

        val scaleValue = ArrayList<String>()
        scaleValue.add("Ground Water Stage")
        scaleValue.add("Total Ann. Recharge")
        scaleValue.add("Current Ann. Extraction")
        scaleValue.add("Current Alloc. For Use")
        scaleValue.add("Rainfall Monsoon Recharge")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(localuser.groundWaterData[5].toFloat(), 1))
        lineEntry.add(Entry(localuser.groundWaterData[2].toFloat(), 2))
        lineEntry.add(Entry(localuser.groundWaterData[3].toFloat(), 3))
        lineEntry.add(Entry(localuser.groundWaterData[9].toFloat(), 4))
        val pieDataSet = PieDataSet(lineEntry, "Year")
        val dataSet = PieData(scaleValue, pieDataSet)
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS)
        binding.pieChart.animateXY(5000, 5000)
        binding.pieChart.setDescription(" ")
        binding.pieChart.data = dataSet


    }


    private fun addData() {
        circularloader(
            localuser.groundWaterData[11].toFloat(),
            300f,
            binding.circularProgressBarGroundWaterStageStats
        )


        binding.apply {
            this.groundwaterstage.text = localuser.groundWaterData[11]
            this.annualDomesticAllocationBy2025.text = localuser.groundWaterData[0]
            this.currentAllocationForUse.text = localuser.groundWaterData[3]
            this.totalAnnualRecharge.text = localuser.groundWaterData[5]
            this.currentAnnualExtraction.text = localuser.groundWaterData[2]
            this.totalNaturalDischarge.text = localuser.groundWaterData[12]
            this.netAvaibilityForFuture.text = localuser.groundWaterData[6]
            this.rainfallMonsoonRecharge.text = localuser.groundWaterData[9]
            this.rainfallNonMonsoonRecharge.text = localuser.groundWaterData[10]

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