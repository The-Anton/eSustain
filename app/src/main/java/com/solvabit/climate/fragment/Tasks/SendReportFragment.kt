package com.solvabit.climate.fragment.Tasks

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentSendReportBinding
import com.solvabit.climate.fragment.Dashboard
import kotlinx.android.synthetic.main.dialog_share_achievement_to_feed.view.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class SendReportFragment : Fragment() {

    private lateinit var binding: FragmentSendReportBinding
    private lateinit var navbar: BottomNavigationView
    private val localUser = Dashboard.localuser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_report, container, false)

        navbar = activity?.findViewById(R.id.bottomNavigation)!!
        navbar.visibility = View.GONE

        initializeData()

        binding.shareReportButton.setOnClickListener {
            showShareIntent()
        }

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
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "tempimage", null)

        // Step 5: Get URI of saved image
        val uri = Uri.parse(path)

        Timber.i("$uri")

        // Step 6: Put Uri as extra to share intent
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // Step 7: Start/Launch Share intent
        startActivity(intent)
    }


    private fun initializeData() {
        binding.apply {
            achievementNameTextView.text = localUser.username
            emailProfile.text = localUser.email
            placeProfile.text = " " + localUser.city + ", " + localUser.state

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

            waterLevelReport.text = localUser.groundWaterData[11].toString()
            annualRechargeReportText.text = localUser.groundWaterData[5].toString()
            annualExtractionReportText.text = localUser.groundWaterData[2].toString()
            naturalDischargeReportText.text = localUser.groundWaterData[12].toString()
            futureAvailableReportText.text = localUser.groundWaterData[6].toString()

            numberTreesPlantedTextView.text = localUser.treesPlanted.toString()
            numberTaskCompletedReport.text = localUser.completedAction.count().toString()
            numberTreesReferredTextView.text = localUser.treesReferred.toString()

        }

    }

    private fun normalizedScoreHeading(): String {
        if (Dashboard.localuser.normalizedScore!=null){
            if(Dashboard.localuser.normalizedScore!! <= 500 ){
                return " You are in Critical Condition!"
            }else if(Dashboard.localuser.normalizedScore!! <= 700 ){
                return "You are on border line !"
            }else{
                return "Congratulation! You are good"
            }
        }else{
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


    private fun getBitmapFromView(view: View) :Bitmap{
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