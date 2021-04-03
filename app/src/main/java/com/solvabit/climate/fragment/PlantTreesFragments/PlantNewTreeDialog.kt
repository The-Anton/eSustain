package com.solvabit.climate.fragment.PlantTreesFragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.PlantedTrees
import com.solvabit.climate.fragment.Dashboard
import com.solvabit.climate.fragment.Trees
import kotlinx.android.synthetic.main.dialog_plant_new_tree.view.*
import timber.log.Timber
import java.util.*

class PlantNewTreeDialog(private val targetTrees: Int, private var treesPlanted: Int) : DialogFragment() {

    private lateinit var dialogView: View
    private val uid = FirebaseAuth.getInstance().uid
    private val localUser = Dashboard.localuser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_plant_new_tree, null)
        builder.setView(dialogView)


        dialogView.add_pic_trees_button_dialog.setOnClickListener {
            uploadImageDataToDevice()
        }

        return builder.create()
    }


    private fun uploadImageDataToDevice() {
        dialogView.add_pic_trees_button_dialog.startAnimation()
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)

    }

    private var selectedPhotoUri: Uri? = null

    private var imageUrl: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val deepColor = Color.parseColor("#27E1EF")
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
            dialogView.add_pic_trees_button_dialog.doneLoadingAnimation(deepColor, largeIcon)
            dialogView.grow_tree_animation_dialog.visibility = View.GONE
            dialogView.add_pic_trees_button_dialog.visibility = View.GONE
            dialogView.imageView_tree_dialog.setImageURI(selectedPhotoUri)
            dialogView.imageView_tree_dialog.visibility = View.VISIBLE
            dialogView.post_image_button_dialog.visibility = View.VISIBLE
            dialogView.checkbox_plant_tree_dialog.visibility = View.VISIBLE

            dialogView.post_image_button_dialog.setOnClickListener {
                dialogView.post_image_button_dialog.startAnimation()
                uploadPhotoToFirebase()
            }

        } else {
            dialogView.add_pic_trees_button_dialog.revertAnimation()
            return
        }
    }

    private fun uploadPhotoToFirebase() {
        if (selectedPhotoUri == null) {
            dialogView.post_image_button_dialog.revertAnimation()
            dialogView.post_image_button_dialog.visibility = View.INVISIBLE
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images-send-by-users/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        val timestamp = System.currentTimeMillis()
                        val dataRef: DatabaseReference
                        if (targetTrees == 5)
                            dataRef = FirebaseDatabase.getInstance()
                                    .getReference("/Users/$uid/fiveTrees/$timestamp")
                        else
                            dataRef = FirebaseDatabase.getInstance()
                                    .getReference("/Users/$uid/tenTrees/$timestamp")
                        treesPlanted += 1
                        val tree = Trees(
                                imageUrl.toString(),
                                System.currentTimeMillis()
                        )

                        dataRef.setValue(tree)
                                .addOnSuccessListener {
                                    if(dialogView.checkbox_plant_tree_dialog.isChecked){
                                        shareOnMaps(imageUrl!!, timestamp)
                                    }
                                    val handler = Handler()
                                    handler.postDelayed({
                                        dialogView.add_pic_trees_button_dialog.revertAnimation()
                                        dialogView.post_image_button_dialog.revertAnimation()
                                        FirebaseDatabase.getInstance().getReference("/Users/$uid")
                                                .child("treesPlanted").setValue(treesPlanted)
                                        dialog?.dismiss()
                                    }, 500)
                                    selectedPhotoUri = null

                                }
                                .addOnFailureListener {
                                    dialogView.add_pic_trees_button_dialog.revertAnimation()
                                    dialogView.post_image_button_dialog.revertAnimation()
                                    dialogView.post_image_button_dialog.visibility = View.INVISIBLE
                                }
                    }
                }
                .addOnFailureListener {
                    dialogView.add_pic_trees_button_dialog.revertAnimation()
                    dialogView.post_image_button_dialog.revertAnimation()
                    dialogView.post_image_button_dialog.visibility = View.INVISIBLE
                }
    }

    private fun shareOnMaps(imageUrl: String, timestamp: Long) {
        Timber.i("Inside share on maps")
        val plantTreeOnMaps = PlantedTrees(
                27.176670,
                78.0081,
                imageUrl,
                timestamp.toString(),
                uid!!,
                localUser.username!!,
                localUser.imageUrl!!
        )
        FirebaseDatabase.getInstance()
                .getReference("/plantedTrees").push().setValue(plantTreeOnMaps)
                .addOnSuccessListener {
                    Timber.i("Planted Tree on map")
                }

    }


}