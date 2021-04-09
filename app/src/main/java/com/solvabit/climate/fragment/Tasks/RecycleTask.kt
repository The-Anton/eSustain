package com.solvabit.climate.fragment.Tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentRecycleTaskBinding


class RecycleTask : Fragment() {

    private lateinit var binding: FragmentRecycleTaskBinding
    private lateinit var navbar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycle_task, container, false)

        navbar = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)!!
        navbar?.visibility = View.GONE

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        navbar?.visibility = View.VISIBLE

    }
}