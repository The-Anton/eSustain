package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.DashboardFragmentBinding
import com.solvabit.climate.viewModel.DashboardViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Dashboard : Fragment() {
    private lateinit var v: View

    companion object {
        fun newInstance() = Dashboard()
        var localuser= User()

    }

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: DashboardFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.dashboard_fragment, container, false
        )

        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val localRepo = Repository(dao, uid)
        if (localuser?.uid == "1") {
            GlobalScope.launch {
                localRepo.getUser { user ->
                    localuser = user
                    Log.v("User", "${user}")
                    //closeAnimation()
                }

            }
        }
        binding.forestMore.setOnClickListener {
            context?.let {
            val popupMenu = PopupMenu(it, binding.forestMore)
            popupMenu.menuInflater.inflate(R.menu.cards_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.Refresh) {
                    Log.d("SignOut", "Signout pressed")
                } else if (it.itemId == R.id.more) {
                    Log.d("Not clicked", "Nothing happened")
                }
                true
            }

            popupMenu.show()
        }
    }

        binding.airMore.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.airMore)
                popupMenu.menuInflater.inflate(R.menu.cards_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.Refresh) {
                        Log.d("SignOut", "Signout pressed")
                    } else if (it.itemId == R.id.more) {
                        Log.d("Not clicked", "Nothing happened")
                    }
                    true
                }

                popupMenu.show()
            }
        }



        return  binding.root
    }




//    fun startAnimation(){
//
//    }
//    fun closeAnimation() {
//        Log.v("User", "Closed Animation")
//
//        v.treeloader.visibility = View.GONE
//        v.dashboardScrollView.visibility = View.VISIBLE
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}