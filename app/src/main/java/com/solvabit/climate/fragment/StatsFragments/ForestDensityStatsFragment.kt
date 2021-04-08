package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import android.os.DropBoxManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentAirQualityStatsBinding
import com.solvabit.climate.databinding.FragmentForestDensityStatsBinding
import com.solvabit.climate.dialog.ForestDialog
import com.solvabit.climate.fragment.Dashboard
import kotlinx.android.synthetic.main.fragment_forest_density_stats.*
import java.security.KeyStore

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
        binding.forestQualityMoreStats.setOnClickListener {
            ForestDialog().show(childFragmentManager, "Forest")
        }

        setLineChart()

        return binding.root
    }

    private fun setLineChart() {

        val xvalue = ArrayList<String>()
        xvalue.add("2011")
        xvalue.add("2012")
        xvalue.add("2014")
        xvalue.add("2015")
        xvalue.add("2019")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(20f, 0))
        lineEntry.add(Entry(10f, 1))
        lineEntry.add(Entry(15f, 2))
        lineEntry.add(Entry(30f, 3))
        lineEntry.add(Entry(10f, 4))

        val lineEntry1 = ArrayList<Entry>()
        lineEntry1.add(Entry(10f, 0))
        lineEntry1.add(Entry(30f, 1))
        lineEntry1.add(Entry(35f, 2))
        lineEntry1.add(Entry(20f, 3))
        lineEntry1.add(Entry(15f, 4))

        val linedataset = LineDataSet(lineEntry, "First")
        linedataset.color = resources.getColor(R.color.green)

        val linedataset1 = LineDataSet(lineEntry1, "Second")
        linedataset1.color = resources.getColor(R.color.blue)

        linedataset.circleRadius = 0f
        linedataset.setDrawFilled(true)
        linedataset.fillColor = resources.getColor(R.color.green)
        linedataset.fillAlpha = 30
        linedataset.setDrawCubic(true)

        linedataset1.circleRadius = 0f
        linedataset1.setDrawFilled(true)
        linedataset1.fillColor = resources.getColor(R.color.blue)
        linedataset1.fillAlpha = 30
        linedataset1.setDrawCubic(true)

        val linedatasetArray = arrayListOf<LineDataSet>(linedataset, linedataset1)
        val data = LineData(xvalue, linedatasetArray as List<ILineDataSet>?)
        binding.lineChart.data = data
        binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
        binding.lineChart.animateXY(3000, 3000)
        binding.lineChart.setTouchEnabled(false)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.getXAxis().setDrawGridLines(false);
        binding.lineChart.getAxisLeft().setDrawGridLines(false);
        binding.lineChart.getAxisRight().setDrawGridLines(false);

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