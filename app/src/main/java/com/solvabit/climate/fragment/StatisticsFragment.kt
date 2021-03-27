package com.solvabit.climate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.solvabit.climate.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*

class StatisticsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_statistics, container, false)
        val statsNavigation = v.findViewById<BottomNavigationView>(R.id.statsNavigation)
        val fragmentContainer = v.findViewById<View>(R.id.statsNavHostFragment)
        val navControllerStats = Navigation.findNavController(fragmentContainer)
        statsNavigation.setupWithNavController(navControllerStats)


//        val navController = Navigation.findNavController(requireActivity() ,R.id.statsNavHostFragment)
//        statsNavigation.setupWithNavController(navController)

        return v

    }


}