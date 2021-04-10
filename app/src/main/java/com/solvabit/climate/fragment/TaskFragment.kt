package com.solvabit.climate.fragment

import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.solvabit.climate.R
import com.solvabit.climate.database.SingleAction
import com.solvabit.climate.databinding.FragmentTaskBinding
import com.solvabit.climate.dialog.StartNewTaskDialog
import timber.log.Timber


class TaskFragment : Fragment(), StartNewTaskDialog.EditNameDialogListener {

    private lateinit var binding: FragmentTaskBinding

    private var localUser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        localUser = Dashboard.localuser
        setRemaining()
        setPresent()
        setClickForTasks()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false)
        binding.allActions = actionsList

        initializeAllTasks()

        setRemaining()

        setPresent()

        setClickForTasks()

        return binding.root
    }

    private fun setClickForTasks() {

        for( i in 1 .. 14) {
            val imageView = returnImageId(i.toString())
            imageView.setImageResource(actionsList[i-1].background)
            var status = "remaining"
            if(localUser.completedAction.contains(i.toString()))
                status = "completed"
            else if(localUser.presentAction.contains(i.toString()))
                status = "present"
            imageView.setOnClickListener {

                if(status=="remaining")
                {
                    showAddTaskDialog(i.toString())
                }
                else
                {
                    navigateToTask(i, status)
                }
            }
        }
    }

    private fun navigateToTask(i: Int, status: String) {
        when(actionsList[i - 1].category){
            tree -> binding.root.findNavController().navigate(
                TaskFragmentDirections.actionTaskFragmentToTreesPlanted(
                    actionsList[i - 1].number,
                    status,
                    i.toString()
                )
            )
            seminar -> binding.root.findNavController()
                .navigate(TaskFragmentDirections.actionTaskFragmentToSendReferral())
            guide -> binding.root.findNavController().navigate(
                TaskFragmentDirections.actionTaskFragmentToGuideTask(
                    i.toString()
                )
            )
            report -> binding.root.findNavController()
                .navigate(TaskFragmentDirections.actionTaskFragmentToSendReportFragment())
            refer -> binding.root.findNavController()
                .navigate(TaskFragmentDirections.actionTaskFragmentToSendReferral())
            recycle -> binding.root.findNavController().navigate(
                TaskFragmentDirections.actionTaskFragmentToRecycleTask(
                    i.toString()
                )
            )
            purchase -> binding.root.findNavController()
                .navigate(TaskFragmentDirections.actionTaskFragmentToSendReferral())
            feed -> binding.root.findNavController()
                .navigate(TaskFragmentDirections.actionTaskFragmentToSendReferral())
        }
    }

    private fun showAddTaskDialog(taskId: String) {
        val fm: FragmentManager = parentFragmentManager
        val startNewTaskDialogFragment =
            StartNewTaskDialog(taskId)
        startNewTaskDialogFragment.setTargetFragment(this, 300)
        startNewTaskDialogFragment.show(fm, "fragment_edit_name")
    }

    override fun onFinishEditDialog(
        taskId: String,
        remainingAction: List<String>,
        presentAction: List<String>
    ) {
        navigateToTask(taskId.toInt(), "present")
        localUser.remainingAction = remainingAction
        localUser.presentAction = presentAction
        setRemaining()
        setPresent()
        setClickForTasks()
    }

    private fun setPresent() {
        localUser.presentAction.forEach {
            val imageView = returnTickId(it)
            imageView.visibility = View.GONE
        }
    }

    private fun setRemaining() {
        Timber.i("Setting Remaining")
        Timber.i("$localUser")
        localUser.remainingAction.forEach {
            val imageView = returnImageId(it)
            val greenTick = returnTickId(it)
            imageView.colorFilter = ColorMatrixColorFilter(matrix)
            greenTick.visibility = View.GONE
        }
    }


    private fun returnTickId(taskId: String): ImageView {
        return when(taskId) {
            "1" -> binding.greenTickAnimationPlantOneTree
            "2" -> binding.greenTickAnimationSeminar
            "3" -> binding.greenTickAnimationConserveElectricity
            "4" -> binding.greenTickAnimationShareReport
            "5" -> binding.greenTickAnimationConserveWater
            "6" -> binding.greenTickAnimationPublicTransport
            "7" -> binding.greenTickAnimationReferFriend
            "8" -> binding.greenTickAnimationRecycleEWaste
            "9" -> binding.greenTickAnimationRecyclePaper
            "10" -> binding.greenTickAnimationReusableShoppingBag
            "11" -> binding.greenTickAnimationWalk
            "12" -> binding.greenTickAnimationPlantFourTree
            "13" -> binding.greenTickAnimationUseCarPool
            "14" -> binding.greenTickAnimationIssueFromFeed
            else -> binding.greenTickAnimationIssueFromFeed
        }
    }


    private fun returnImageId(taskId: String): ImageView {
        return when(taskId) {
            "1" -> binding.plantOneTreeImageViewTasks
            "2" -> binding.seminarImageViewTasks
            "3" -> binding.conserveElectricityImageViewTasks
            "4" -> binding.shareReportImageViewTasks
            "5" -> binding.conserveWaterImageViewTasks
            "6" -> binding.publicTransportImageViewTasks
            "7" -> binding.referFriendImageViewTasks
            "8" -> binding.recycleEWasteImageViewTasks
            "9" -> binding.recyclePaperImageViewTasks
            "10" -> binding.reusableShoppingBagImageViewTasks
            "11" -> binding.walkImageViewTasks
            "12" -> binding.plantFourTreeImageViewTasks
            "13" -> binding.useCarPoolImageViewTasks
            "14" -> binding.issueFromFeedImageViewTasks
            else -> binding.issueFromFeedImageViewTasks
        }
    }

    private fun initializeAllTasks() {

        actionsList.add(
            SingleAction(
                1,
                "Plant a Tree",
                "Begin with a single step",
                "tree",
                1,
                R.drawable.plant_one_tree_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                2,
                "Seminar",
                "Attend sustainable environment seminar",
                "seminar",
                1,
                R.drawable.seminar_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                3,
                "Save electricity",
                "Energy is life",
                "guide",
                1,
                R.drawable.save_electricity_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                4,
                "Share Report",
                "Help your family and friends too",
                "report",
                1,
                R.drawable.share_report_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                5,
                "Save water",
                "Remember water cycle & lifecycle are one",
                "guide",
                1,
                R.drawable.save_water_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                6,
                "Public Transport",
                "The share of public transport is just 18.1% of work trips.",
                "guide",
                1,
                R.drawable.public_transport_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                7,
                "Refer friends",
                "Let's inspire others for a sustainable future",
                "refer",
                5,
                R.drawable.refer_friend_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                8,
                "E-waste",
                "India generates about 3 million tonnes of e-waste annually and ranks third among e-waste producing countries",
                "recycle",
                1,
                R.drawable.ewaste_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                9,
                "Recycle Paper",
                "To produce a ton of paper we need about 115,000 liters of water",
                "recycle",
                1,
                R.drawable.recycle_paper_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                10,
                "Reusable bag",
                "India generates nearly 26,000 tonnes of plastic waste every day",
                "purchase",
                1,
                R.drawable.reusable_bag_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                11,
                "Take a walk",
                "Walking is fun and environment friendly",
                "guide",
                1,
                R.drawable.walk_round_icon,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                12,
                "Plant four tree",
                "In Chicago, trees remove more than 18,000 tons of air pollution each year.",
                "tree",
                4,
                R.drawable.plant_four_trees_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                13,
                "Use Carpool",
                "If you carpool at least twice a week, you can help reduce the emission of 1,600 pounds of greenhouse gases by a car each year",
                "guide",
                1,
                R.drawable.use_carpool_round_icons,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
        actionsList.add(
            SingleAction(
                14,
                "Solve an Issue",
                "Nature is waiting for you!!",
                "feed",
                1,
                R.drawable.walk_round_icon,
                "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX36746250.jpg"
            )
        )
    }

    companion object{
        val matrix = floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 0.5f, 0f
        )
        const val tree = "tree"
        const val seminar = "seminar"
        const val guide = "guide"
        const val report = "report"
        const val refer = "refer"
        const val recycle = "recycle"
        const val purchase = "purchase"
        const val feed = "feed"
        val actionsList = mutableListOf<SingleAction>()
    }
}