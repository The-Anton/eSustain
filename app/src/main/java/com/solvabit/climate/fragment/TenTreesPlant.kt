package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solvabit.climate.R
import com.solvabit.climate.viewModal.TenTreesPlantViewModel

class TenTreesPlant : Fragment() {

    companion object {
        fun newInstance() = TenTreesPlant()
    }

    private lateinit var viewModel: TenTreesPlantViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ten_trees_plant_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TenTreesPlantViewModel::class.java)
        // TODO: Use the ViewModel
    }

}