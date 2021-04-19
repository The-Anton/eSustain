package com.solvabit.climate.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.fragment.Dashboard
import timber.log.Timber


class StartNewTaskDialog(val taskId: String) : DialogFragment() {

    private val localUser = Dashboard.localuser

    interface EditNameDialogListener {
        fun onFinishEditDialog(
            taskId: String,
            remainingAction: List<String>,
            presentAction: List<String>
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_start_new_task, null)
        builder.setView(dialogView)

        val joinTaskBtn = dialogView.findViewById<Button>(R.id.start_new_task_button_dialog)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_button_start_new_task_dialog)

        Timber.i("Inside dialog")

        joinTaskBtn.setOnClickListener {
            Toast.makeText(context, "Adding task", Toast.LENGTH_SHORT).show()
            val uid = FirebaseAuth.getInstance().uid
            val changePresentList =
                localUser.presentAction.toMutableList()
            val changeRemainingList =
                localUser.remainingAction.toMutableList()
            if (changeRemainingList.indexOf(taskId) != -1)
                changeRemainingList.removeAt(changeRemainingList.indexOf(taskId))
            if (changePresentList.indexOf(taskId) == -1)
                changePresentList.add(taskId)
            FirebaseDatabase.getInstance().getReference("/Users/$uid/presentAction")
                .setValue(changePresentList)
            FirebaseDatabase.getInstance().getReference("/Users/$uid/remainingAction")
                .setValue(changeRemainingList)
                .addOnSuccessListener {
                    Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show()
                    val listener: EditNameDialogListener = targetFragment as EditNameDialogListener
                    listener.onFinishEditDialog(taskId, changeRemainingList, changePresentList)
                    dialog?.dismiss()
                }

        }

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        return builder.create()
    }
}