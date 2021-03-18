package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solvabit.climate.R
import com.solvabit.climate.viewModal.TreesPlantedViewModel

class TreesPlanted : Fragment() {

    companion object {
        fun newInstance() = TreesPlanted()
    }

    private lateinit var viewModel: TreesPlantedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trees_planted_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TreesPlantedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}