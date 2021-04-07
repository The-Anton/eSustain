package com.solvabit.climate.fragment.PlantTreesFragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.fragment.Dashboard
import com.solvabit.climate.fragment.Trees
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_share_achievement_to_feed.view.*
import timber.log.Timber
import java.io.ByteArrayOutputStream


class ShareAchievementToPostDialog(private val allTreesPlanted: List<Trees>) : DialogFragment() {

    private lateinit var dialogView: View
    private val uid = FirebaseAuth.getInstance().uid
    private val localUser = Dashboard.localuser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_share_achievement_to_feed, null)
        builder.setView(dialogView)

        Picasso.get().load(localUser.imageUrl).into(dialogView.user_image)
        dialogView.username.text = localUser.username
        Picasso.get().load(localUser.imageUrl).into(dialogView.user_image_achievement_bitmap)
        Picasso.get().load(R.drawable.blue_tick).into(dialogView.logo_achievement_bitmap)

        recyclerViewFunction()

        dialogView.postButton.setOnClickListener {
            hideKeyboardFrom(requireContext(), dialogView)
            Handler(Looper.getMainLooper()).postDelayed({
                createPost()
            }, 500)
        }


        return builder.create()
    }

    private fun recyclerViewFunction() {
        val multiImage = dialogView.findViewById<RecyclerView>(R.id.post_image_multi_image_dialog)

        val allImages = mutableListOf<String>()
        val allDates = mutableListOf<Long>()
        allTreesPlanted.forEach {
            allImages.add(it.treeImage)
            allDates.add(it.timestamp)
        }

        var spanCount = 2
        if (allImages.size == 10)
            spanCount = 5
        StaggeredGridLayoutManager(
                spanCount, // span count
                StaggeredGridLayoutManager.VERTICAL // orientation
        ).apply {
            // specify the layout manager for recycler view
            multiImage.layoutManager = this
        }

        // finally, data bind the recycler view with adapter
        multiImage.adapter = StaggeredRecyclerViewAdapter(allImages)
    }

    private fun createPost() {
        if (dialogView.post_text.text.isEmpty()) {
            Toast.makeText(requireContext(), "Sorry, Blank can't be posted!", Toast.LENGTH_SHORT).show()
        } else {
            Timber.i("Going to show dialog box")
            com.solvabit.climate.dialog.Dialog().show(childFragmentManager, "MyCustomFragment")
            val bitmap = getBitmapFromView(dialogView.linear_layout_grid_images_dialog)
            bitmap?.scale(100, 100)
            uploadImage(bitmap!!)
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid/tenTreesPlant/${bitmap.toString()}")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            Timber.i("Upload Failed")
        }.addOnSuccessListener { taskSnapshot ->
            ref.downloadUrl
                    .addOnSuccessListener {
                        Timber.i("Download Url - ${it.toString()}")
                        post(it.toString())
                    }

        }

    }

    private fun post(postImageUrl: String) {
        val time = System.currentTimeMillis().toString()
        val max = Long.MAX_VALUE
        val timestamp = max - System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/PostData/$timestamp")
        val text = dialogView.post_text.text.toString()
        val postData = Post(text, postImageUrl, uid!!, time, "Achievement", timestamp.toString(), "", 0)
        ref.setValue(postData).addOnSuccessListener {
            Toast.makeText(requireContext(), "Posted Successfully!!", Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getBitmapFromView(view: View): Bitmap? {
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
        //return the bitmap
        return returnedBitmap
    }
}