package com.solvabit.climate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.solvabit.climate.R
import com.solvabit.climate.fragment.StatsFragments.AirQualityStatsFragment
import com.solvabit.climate.fragment.StatsFragments.ForestDensityStatsFragment
import com.solvabit.climate.fragment.StatsFragments.GroundWaterStatsFragment
import com.solvabit.climate.fragment.StatsFragments.StatsViewPagerAdapter
import com.solvabit.climate.onboarding.ViewPagerAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.aqui_intro.view.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*

class StatisticsFragment : Fragment() {

    lateinit var v: View
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout
    val localuser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_statistics, container, false)

        viewPager = v.findViewById(R.id.statsViewPager)
        tabLayout = v.findViewById(R.id.statsNavigationTabLayout)


        v.username_stats.text = localuser.username.toString()
        v.place_stats.text = " " + localuser.city + ", " + localuser.state
        Picasso.get().load(localuser.imageUrl).into(v.userprofile_imageView_stats)

        return v

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        /* tabLayout.getTabAt(0)!!.setIcon(R.drawable.air)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.forestree)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.drop) */

    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = StatsViewPagerAdapter(childFragmentManager)
        adapter.addFragment(AirQualityStatsFragment(), "Air")
        adapter.addFragment(ForestDensityStatsFragment(), "Forest")
        adapter.addFragment(GroundWaterStatsFragment(), "Water")
        viewPager.adapter = adapter
    }
}