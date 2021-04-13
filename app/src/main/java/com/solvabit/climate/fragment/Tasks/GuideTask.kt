package com.solvabit.climate.fragment.Tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.solvabit.climate.R
import com.solvabit.climate.databinding.FragmentGuideTaskBinding
import com.solvabit.climate.fragment.Dashboard
import com.solvabit.climate.fragment.TaskFragment
import kotlinx.android.synthetic.main.fragment_guide_task.*


class GuideTask : Fragment() {


    private lateinit var binding: FragmentGuideTaskBinding
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var navbar: BottomNavigationView
    private val actionsList = Dashboard.actionsList
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

        initalizeVideoPlayer(taskId)

        initateQuiz(taskId)
        return binding.root
    }

    private fun initalizeVideoPlayer(taksId: Int) {
        youTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        var videoId = "iLLYX3RbtPQ"

        when(taksId){
            3 -> videoId = "23XQ-hQoR7A"
            5 -> videoId = "iLLYX3RbtPQ"
            6 -> videoId = "NDig_mkApg8"
            11 -> videoId = "qFNFLT9Y8eQ"
            13 -> videoId = "-t6W7hG1sBU"
        }
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                youTubePlayer.loadVideo(videoId, 0F)
            }
        })
    }


    private fun initateQuiz(taksId: Int) {
        var quizContainer = binding.quizContainer
        val inflater = LayoutInflater.from(context)
        when(taksId){
            3 -> inflater.inflate(R.layout.conserve_electricity_quiz,quizContainer, true)

            5 -> inflater.inflate(R.layout.conserve_water_quiz,quizContainer, true)


        }

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