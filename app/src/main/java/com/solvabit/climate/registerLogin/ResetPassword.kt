package com.solvabit.climate.registerLogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        button_reset_password.setOnClickListener {

            val emailAddress = email_editText_reset.text.toString()

            FirebaseAuth.getInstance().sendPasswordResetEmail("chaharchandresh@gmail.com")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        button_reset_password.text = "Reset link sent"
                        Log.d(TAG, "Email sent.")
                    }
                }

        }
    }
    companion object
    {
        val TAG = "resetPassword"
    }
}