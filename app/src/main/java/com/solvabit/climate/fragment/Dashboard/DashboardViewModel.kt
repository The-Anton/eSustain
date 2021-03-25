package com.solvabit.climate.fragment.Dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.progressview.progressView
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import kotlinx.coroutines.*
import timber.log.Timber

//class DashboardViewModel(
//        val database: UserDao,
//        application: Application) : AndroidViewModel(application){
//
//    override fun onCleared() {
//        super.onCleared()
//        viewModelJob.cancel()
//        Timber.i("ViewModel Deleted!!!!")
//    }
//
//    private var viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
//    private var localUser = MutableLiveData<User>()
//    private val uid = FirebaseAuth.getInstance().uid
//
//    init {
//        initializeUser()
//    }
//
//    private fun initializeUser() {
//        uiScope.launch {
//            localUser.value = getLocalUser()
//            Timber.i("localUser - ${localUser.value}")
//        }
//    }
//
//    private suspend fun getLocalUser() : User? {
//        return withContext(Dispatchers.IO){
//            database.getUserByUID(uid!!)
//        }
//    }
//
//}