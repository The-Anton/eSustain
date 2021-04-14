package com.solvabit.climate.registerLogin

import androidx.lifecycle.ViewModel
import timber.log.Timber

class RegisterViewModel : ViewModel() {
    init {
        Timber.i("RegisterViewModel")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("RegisterViewModel destroyed")
    }
}