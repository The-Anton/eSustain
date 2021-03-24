package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solvabit.climate.databinding.ProfileFragmentBinding
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.solvabit.climate.R
import com.solvabit.climate.databinding.DashboardFragmentBinding
import com.solvabit.climate.fragment.Dashboard.Companion.localuser
import com.solvabit.climate.viewModel.ProfileViewModel

class Profile : Fragment() {

    companion object {
        fun newInstance() = Profile()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: ProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment, container, false
        )
        Log.v("Profile", localuser.toString())
        binding.logoutPopup?.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.logoutPopup)
                popupMenu.menuInflater.inflate(R.menu.nav_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.sign_out_menu) {
                        Log.d("SignOut", "Signout pressed")
                    } else {
                        Log.d("Not clicked", "Nothing happened")
                    }
                    true
                }

                popupMenu.show()
            }

        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}