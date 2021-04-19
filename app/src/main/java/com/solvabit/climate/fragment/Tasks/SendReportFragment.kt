package com.solvabit.climate.fragment.Tasks

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.FragmentSendReportBinding
import com.solvabit.climate.fragment.Dashboard.Companion.localuser
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream


class SendReportFragment : Fragment() {

    private lateinit var binding: FragmentSendReportBinding
    private lateinit var navbar: BottomNavigationView
    private val localUser = localuser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_report, container, false)

        navbar = activity?.findViewById(R.id.bottomNavigation)!!
        navbar.visibility = View.GONE
        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()
        val statsDao = instance.statsDao()
        val localRepo = Repository(dao, uid)
        initializeData()

        binding.shareReportButton.setOnClickListener {
            showShareIntent()
        }

        val stats = Stats()
        setLineChart(stats)
        GlobalScope.launch(Dispatchers.Main) {
            localRepo.getStats(statsDao) {
                Log.v("StatsFragment", it.toString())
                setLineChart(it)
            }

        }
        setPieChart()
        return binding.root
    }


    private fun showShareIntent() {
        // Step 1: Create Share itent
        val intent = Intent(Intent.ACTION_SEND).setType("image/*")

        // Step 2: Get Bitmap from your imageView
        val bitmap = getBitmapFromView(binding.reportConstraintLayout)// your imageView here.

        // Step 3: Compress image
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        // Step 4: Save image & get path of it
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "tempimage",
            null
        )

        // Step 5: Get URI of saved image
        val uri = Uri.parse(path)

        Timber.i("$uri")

        // Step 6: Put Uri as extra to share intent
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // Step 7: Start/Launch Share intent
        startActivity(intent)
    }

    private fun setLineChart(stats: Stats) {

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


        val linedataset = LineDataSet(lineEntry, "AFC")
        linedataset.color = resources.getColor(R.color.green)

        val linedataset1 = LineDataSet(lineEntry1, "OFC")
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

        val actualCoverData = LineData(xvalue, linedatasetArray as List<ILineDataSet>?)

        binding.lineChart.data = actualCoverData
        binding.lineChart.setBackgroundColor(resources.getColor(R.color.white))
        binding.lineChart.animateXY(3000, 3000)
        binding.lineChart.setTouchEnabled(false)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.setDescription(" ")
        binding.lineChart.axisLeft.setDrawGridLines(false)
        binding.lineChart.axisRight.setDrawGridLines(false)

    }


    private fun setPieChart() {

        val scaleValue = java.util.ArrayList<String>()
        scaleValue.add("GWS")
        scaleValue.add("TAR")
        scaleValue.add("CAE")
        scaleValue.add("CAU")
        scaleValue.add("RMR")

        val lineEntry = java.util.ArrayList<Entry>()
        lineEntry.add(Entry(localuser.groundWaterData[5].toFloat(), 1))
        lineEntry.add(Entry(localuser.groundWaterData[2].toFloat(), 2))
        lineEntry.add(Entry(localuser.groundWaterData[3].toFloat(), 3))
        lineEntry.add(Entry(localuser.groundWaterData[9].toFloat(), 4))
        val pieDataSet = PieDataSet(lineEntry, "")
        val dataSet = PieData(scaleValue, pieDataSet)
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS)
        binding.pieChart.animateXY(5000, 5000)
        binding.pieChart.setDrawSliceText(true)
        binding.pieChart.setDescription(" ")
        binding.pieChart.data = dataSet


    }

    private fun initializeData() {
        binding.apply {
            achievementNameTextView.text = localUser.username
            emailProfile.text = localUser.email
            placeProfile.text = " " + localUser.city + ", " + localUser.state
            Picasso.get().load(localUser.imageUrl).into(this.userprofileImageView)

            normalizedScoreReport.text = localUser.normalizedScore?.toInt().toString()
            normalizedScoreHeadingReport.text = normalizedScoreHeading()

            aqiReport.text = localUser.aqi.toString()
            pm10ReportText.text = String.format("%.2f", localUser.pm10)
            pm25ReportText.text = String.format("%.2f", localUser.pm25)
            coReportText.text = String.format("%.2f", localUser.co)
            o3ReportText.text = String.format("%.2f", localUser.o3)

            forestDensityReport.text = String.format("%.2f", localUser.forestDensity)
            totalAreaReportText.text = localUser.totalArea.toString()
            actualForestReportText.text = localUser.actualForest.toString()
            openForestReportText.text = localUser.openForest.toString()
            noForestReportText.text = localUser.noForest.toString()

            waterLevelReport.text = localUser.groundWaterData[11]
            annualRechargeReportText.text = localUser.groundWaterData[5]
            annualExtractionReportText.text = localUser.groundWaterData[2]
            naturalDischargeReportText.text = localUser.groundWaterData[12]
            futureAvailableReportText.text = localUser.groundWaterData[6]

            numberTreesPlantedTextView.text = localUser.treesPlanted.toString()
            numberTaskCompletedReport.text = localUser.completedAction.count().toString()
            numberTreesReferredTextView.text = localUser.treesReferred.toString()

        }

    }

    private fun normalizedScoreHeading(): String {
        if (localuser.normalizedScore != null) {
            if (localuser.normalizedScore!! <= 500) {
                return " You are in Critical Condition!"
            } else if (localuser.normalizedScore!! <= 700) {
                return "You are on border line !"
            } else {
                return "Congratulation! You are good"
            }
        } else {
            return "Congratulation! You are good"
        }
    }


//    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String) { // File name like "image.png"
//        //create a file to write bitmap data
//        var file: File? = null
//        try {
//            file = File(
//                Environment.getExternalStorageDirectory()
//                    .toString() + File.separator + fileNameToSave
//            )
//            file.createNewFile()
//
//            //Convert bitmap to byte array
//            val bos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos) // YOU can also save it in JPEG
//            val bitmapdata = bos.toByteArray()
//
//            //write the bytes in file
//            val fos = FileOutputStream(file)
//            fos.write(bitmapdata)
//            fos.flush()
//            fos.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    private fun getBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)

        return returnedBitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        navbar.visibility = View.VISIBLE
    }

}