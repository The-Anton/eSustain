package com.solvabit.climate.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.forests.onboarding.screens.First_Screen
import com.example.forests.onboarding.screens.Second_Screen
import com.example.forests.onboarding.screens.Third_Screen

class ViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return First_Screen()
            1 -> return Second_Screen()
            2 -> return Third_Screen()
            else -> return First_Screen()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}