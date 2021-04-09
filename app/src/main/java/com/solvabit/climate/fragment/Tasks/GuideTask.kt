package com.solvabit.climate.fragment.Tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentGuideTaskBinding
import com.solvabit.climate.fragment.TaskFragment


class GuideTask : Fragment() {


    private lateinit var binding: FragmentGuideTaskBinding
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var navbar: BottomNavigationView
    private val actionsList = TaskFragment.actionsList
    private var taskId = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide_task, container, false)

        val args = GuideTaskArgs.fromBundle(requireArguments())
        taskId = args.taskId.toInt()

        navbar = activity?.findViewById(R.id.bottomNavigation)!!
        navbar.visibility = View.GONE

        initalizeToolbar()

        initalizeVideoPlayer()

        return binding.root
    }

    private fun initalizeVideoPlayer() {
        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "iLLYX3RbtPQ"
                youTubePlayer.loadVideo(videoId, 0F)
            }
        })
    }

    private fun initalizeToolbar() {
        binding.guideTitle.text = actionsList[taskId - 1].title.toString()
        binding.backArrowGuide.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
        navbar.visibility = View.VISIBLE
    }
}