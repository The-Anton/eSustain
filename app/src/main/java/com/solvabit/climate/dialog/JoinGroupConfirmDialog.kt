package com.solvabit.climate.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.fragment.Dashboard
import kotlinx.android.synthetic.main.dialog_join_group_confirm.view.*
import timber.log.Timber

class JoinGroupConfirmDialog(val post: Post) : DialogFragment() {

    private val localUser = Dashboard.localuser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_join_group_confirm, null)
        builder.setView(dialogView)

        val joinGroupBtn = dialogView.findViewById<Button>(R.id.join_button_join_group_dialog)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_button_join_group_dialog)

        Timber.i("Inside dialog")

        joinGroupBtn.setOnClickListener {
            Toast.makeText(context, "Joining group", Toast.LENGTH_SHORT).show()
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/PostData/${post.key}/interestedUsers/$uid")
            ref.setValue(true)
                    .addOnSuccessListener {
                        val postLists = localUser.interestedGroups.toMutableList()
                        postLists.add(post)
                        FirebaseDatabase.getInstance().getReference("/Users/$uid/interestedGroups")
                                .setValue(postLists)
                                .addOnSuccessListener {
                                    dialogView.confirm_join_group_textView.text = "Congratulations!! \n You can check your newly added group now"
                                    Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show()
                                }
                    }

        }

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        return builder.create()
    }
}