package com.solvabit.climate.fragment.StatsFragments

import android.os.Bundle
import android.os.DropBoxManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.FragmentAirQualityStatsBinding
import com.solvabit.climate.databinding.FragmentForestDensityStatsBinding
import com.solvabit.climate.dialog.ForestDialog
import com.solvabit.climate.fragment.Dashboard
import kotlinx.android.synthetic.main.fragment_forest_density_stats.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()
        val statsDao = instance.statsDao()
        val localRepo = Repository(dao, uid)

        addData()
        binding.forestQualityMoreStats.setOnClickListener {
            ForestDialog().show(childFragmentManager, "Forest")
        }

        val stats = Stats()
        setLineChart(stats)
        GlobalScope.launch(Dispatchers.Main) {
            localRepo.getStats(statsDao){
                Log.v("StatsFragment",it.toString())
                setLineChart(it)
            }

        }

        return binding.root
    }


    private fun setLineChart(stats:Stats) {

        val xvalue = ArrayList<String>()
        xvalue.add("2011")
        xvalue.add("2012")
        xvalue.add("2014")
        xvalue.add("2015")
        xvalue.add("2019")

        val lineEntry = ArrayList<Entry>()
        lineEntry.add(Entry(stats.actual_forest_cover_2009.toFloat(), 0))
        lineEntry.add(Entry(stats.actual_forest_cover_2011.toFloat(), 1))
        lineEntry.add(Entry(stats.actual_forest_cover_2013.toFloat(), 2))
        lineEntry.add(Entry(stats.actual_forest_cover_2015.toFloat(), 3))
        lineEntry.add(Entry(stats.actual_forest_cover_2019.toFloat(), 4))

        val lineEntry1 = ArrayList<Entry>()
        lineEntry1.add(Entry(stats.open_forest_2009.toFloat(), 0))
        lineEntry1.add(Entry(stats.open_forest_2011.toFloat(), 1))
        lineEntry1.add(Entry(stats.open_forest_2013.toFloat(), 2))
        lineEntry1.add(Entry(stats.open_forest_2015.toFloat(), 3))
        lineEntry1.add(Entry(stats.open_forest_2019.toFloat(), 4))

        val lineEntry2 = ArrayList<Entry>()
        lineEntry2.add(Entry(stats.percentage_of_geographical_area_2009.toFloat(), 0))
        lineEntry2.add(Entry(stats.percentage_of_geographical_area_2011.toFloat(), 1))
        lineEntry2.add(Entry(stats.percentage_of_geographical_area_2013.toFloat(), 2))
        lineEntry2.add(Entry(stats.percentage_of_geographical_area_2015.toFloat(), 3))
        lineEntry2.add(Entry(stats.percentage_of_geographical_area_2019.toFloat(), 4))

        val linedataset = LineDataSet(lineEntry, "Actual Forest Cover(in Sq.Km)")
        linedataset.color = resources.getColor(R.color.green)

        val linedataset1 = LineDataSet(lineEntry1, "Open Forest Cover(in Sq.Km)")
        linedataset1.color = resources.getColor(R.color.blue)

        val linedataset2 = LineDataSet(lineEntry2, "Percentage % of total forest cover")
        linedataset2.color = resources.getColor(R.color.orange)

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

        linedataset2.circleRadius = 0f
        linedataset2.setDrawFilled(true)
        linedataset2.fillColor = resources.getColor(R.color.orange)
        linedataset2.fillAlpha = 30
        linedataset2.setDrawCubic(true)

        val linedatasetArray = arrayListOf<LineDataSet>(linedataset, linedataset1)
        val linedatasetArray2 = arrayListOf<LineDataSet>(linedataset2)

        val actualCoverData = LineData(xvalue, linedatasetArray as List<ILineDataSet>?)
        val percentageData = LineData(xvalue, linedatasetArray2 as List<ILineDataSet>?)

        binding.lineChart.data = actualCoverData
        binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
        binding.lineChart.animateXY(3000, 3000)
        binding.lineChart.setTouchEnabled(false)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.getXAxis().setDrawGridLines(false);
        binding.lineChart.getAxisLeft().setDrawGridLines(false);
        binding.lineChart.getAxisRight().setDrawGridLines(false);

        binding.lineChart2.data = percentageData
        binding.lineChart2.setBackgroundColor(resources.getColor(R.color.white))
        binding.lineChart2.animateXY(3000, 3000)
        binding.lineChart2.setTouchEnabled(false)
        binding.lineChart2.setDrawGridBackground(false)
        binding.lineChart2.getXAxis().setDrawGridLines(false);
        binding.lineChart2.getAxisLeft().setDrawGridLines(false);
        binding.lineChart2.getAxisRight().setDrawGridLines(false);
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