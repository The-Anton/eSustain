package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solvabit.climate.R
import com.solvabit.climate.viewModel.SendReferralViewModel

class SendReferral : Fragment() {

    companion object {
        fun newInstance() = SendReferral()
    }

    private lateinit var viewModel: SendReferralViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.send_referral_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SendReferralViewModel::class.java)
        // TODO: Use the ViewModel
    }

}