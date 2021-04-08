package com.solvabit.climate.fragment

import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentTaskBinding


class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false)


        val matrix = floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 0.5f, 0f)
        binding.conserveElectricityImageViewTasks.colorFilter = ColorMatrixColorFilter(matrix)
//        binding.seminarImageViewTasks.colorFilter = ColorMatrixColorFilter(matrix)
        binding.shareReportImageViewTasks.colorFilter = ColorMatrixColorFilter(matrix)

        binding.greenTickAnimationConserveElectricity.visibility = View.GONE
        binding.greenTickAnimationSeminar.visibility = View.GONE
        binding.greenTickAnimationShareReport.visibility = View.GONE



        return binding.root
    }

}