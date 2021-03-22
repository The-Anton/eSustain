package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.viewModel.DashboardViewModel
import kotlinx.android.synthetic.main.dashboard_fragment.*
import kotlinx.android.synthetic.main.dashboard_fragment.view.*
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
        v = inflater.inflate(R.layout.dashboard_fragment, container, false)

        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val localRepo = Repository(dao,uid)
        if (localuser?.uid == "1"){
            GlobalScope.launch{
                localRepo.getUser{
                    user -> localuser = user
                    Log.v("User", "${user}")
                    //closeAnimation()
                }

            }
        }


        return  v
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