package com.solvabit.climate.fragment.Tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentRecycleTaskBinding
import com.solvabit.climate.fragment.TaskFragment


class RecycleTask : Fragment() {

    private lateinit var binding: FragmentRecycleTaskBinding
    private lateinit var navbar: BottomNavigationView
    private val actionsList = TaskFragment.actionsList
    private var taskId = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recycle_task, container, false)

        val args = GuideTaskArgs.fromBundle(requireArguments())
        taskId = args.taskId.toInt()

        navbar = activity?.findViewById(R.id.bottomNavigation)!!
        navbar.visibility = View.GONE

        initializeToolbar()
        binding.button.setOnClickListener {
            val action = RecycleTaskDirections.actionRecycleTaskToCreatePostFragment(true)
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun initializeToolbar() {
        binding.recycleTitle.text = actionsList[taskId - 1].title
        binding.backArrowRecycle.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        navbar.visibility = View.VISIBLE
    }
}