package com.solvabit.climate.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cuberto.liquid_swipe.LiquidPager
import com.solvabit.climate.R

class Onboarding : AppCompatActivity() {


    lateinit var pager: LiquidPager
    lateinit var adapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        adapter = ViewPagerAdapter(supportFragmentManager)
        pager = findViewById<LiquidPager>(R.id.pager)
        pager.adapter = adapter

    }
}