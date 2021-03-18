package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.forests.data.airQualityDataService
import com.example.forests.data.airQualityResponse.Data
import com.example.forests.data.parametersDataService
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.network.parametersData
import com.solvabit.climate.viewModal.DashboardViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Dashboard : Fragment() {

    companion object {
        fun newInstance() = Dashboard()
    }

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val localRepo = Repository(dao,uid)

        GlobalScope.launch{
            var user = localRepo.getUser{
                user ->
                Log.v("User", "${user}")

            }

//            lateinit var parametersData: parametersData
//            val apiService = parametersDataService()
//            val response = apiService?.getData(uid,30.3752 , 76.7821)?.await()
//            if (response != null) {
//
//                Log.i("AirQualityAPIresponse", response.toString())
//            }else{
//                Log.i("AirQualityAPIresponse", "NO data fetched")
//
//            }

        }
        return inflater.inflate(R.layout.dashboard_fragment, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        // TODO: Use the ViewModel



    }

}