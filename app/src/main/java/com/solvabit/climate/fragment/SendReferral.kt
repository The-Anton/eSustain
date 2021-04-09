package com.solvabit.climate.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.databinding.SendReferralFragmentBinding
import com.solvabit.climate.viewModel.SendReferralViewModel
import timber.log.Timber

class SendReferral : Fragment() {

    companion object {
        fun newInstance() = SendReferral()
    }

    private lateinit var binding: SendReferralFragmentBinding

    private lateinit var viewModel: SendReferralViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.send_referral_fragment, container, false
        )

        Timber.i("Uid of user: ${Dashboard.localuser.uid}")
        binding.sendReferralBtn.setOnClickListener {
            sendReferralIntent()
        }

        return binding.root
    }

    private fun sendReferralIntent() {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey, let's move towards a Sustainable Future. \n" +
                        "I am using this cool app, Give it a try :) \n" +
                        "https://forests.page.link/Referral")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivityForResult(shareIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1 && resultCode== Activity.RESULT_OK)
        {
            val uid = FirebaseAuth.getInstance().uid
            var addReferral = Dashboard.localuser.treesReferred?.plus(1)
            FirebaseDatabase.getInstance().getReference("/Users/$uid/treesReferred").setValue(addReferral)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SendReferralViewModel::class.java)
    }

}