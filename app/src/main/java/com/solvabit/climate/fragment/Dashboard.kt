package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.SingleAction
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.DashboardFragmentBinding
import com.solvabit.climate.dialog.AqiDialog
import com.solvabit.climate.dialog.ForestDialog
import com.solvabit.climate.dialog.StartNewTaskDialog
import com.solvabit.climate.viewModel.DashboardViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.recommended_cards.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class Dashboard : Fragment() {

    companion object {
        var localuser = User()
        val actionsList = mutableListOf<SingleAction>()
    }

    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dashboard_fragment, container, false
        )

        val uid = FirebaseAuth.getInstance().uid.toString()
        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val localRepo = Repository(dao, uid)
        if (localuser.uid == "1") {
            GlobalScope.launch(Dispatchers.Main) {
                localRepo.getUser { user ->
                    localuser = user
                    Timber.i("$user")
                    addDataToDashboard()
                }
            }
        }
        addDataToDashboard()

        binding.forestMore.setOnClickListener {
            AqiDialog().show(childFragmentManager, "AQId")
        }
        binding.airMore.setOnClickListener {
            ForestDialog().show(childFragmentManager, "Forest")
        }

        GlobalScope.launch {
            Repository(dao, uid).fetchUpdates {
                addDataToDashboard()
                addRecommendedDashboardItems(localuser)
            }
        }


        return binding.root
    }


    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

    }

    private fun addDataToDashboard() {
        binding.normalizedScoreData.text = localuser.normalizedScore.toString()
        binding.airQualityData.text = localuser.aqi.toString()
        binding.forestDensityData.text = localuser.forestDensity.toString()
        val normalizedScore: Float = localuser.normalizedScore?.toFloat() ?: 0f

        if (localuser.normalizedScore != null) {
            if (localuser.normalizedScore!! <= 500) {
                binding.statusText.text = " You are in Critical Condition!"
                binding.linearLayout3.setBackgroundResource(R.drawable.red_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_block_24)

                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_block_24)
            } else if (localuser.normalizedScore!! <= 700) {
                binding.statusText.text = "  You are on border line !"
                binding.linearLayout3.setBackgroundResource(R.drawable.orange_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_warning_24)
            } else {
                binding.statusText.text = "  Congratulation! You are good"
                binding.linearLayout3.setBackgroundResource(R.drawable.home_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
            }
        } else {
            binding.statusText.text = "  Congratulation! You are good"
            binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
        }

        binding.circularProgressBar.progress = normalizedScore / 10
        circularloader(localuser.aqi?.toFloat() ?: 0f, 500f, binding.circularProgressBarAirQuality)
        initializeAirData()
        initializeForestData()

    }

    private fun initializeAirData() {
        binding.apply {
            this.coAirQuality.text = localuser.co.toString()
            this.so2AirQuality.text = localuser.so2.toString()
            this.o3AirQuality.text = localuser.o3.toString()
            this.no2AirQuality.text = localuser.no2.toString()
        }
    }

    private fun initializeForestData() {
        circularloader(
            localuser.forestDensity?.toFloat() ?: 0f,
            10f,
            binding.circularProgressBarForestDensity
        )
        binding.apply {
            this.actualForestCover.text = localuser.actualForest.toString()
            this.openForestCover.text = localuser.openForest.toString()
            this.noForestCover.text = localuser.noForest.toString()
            this.totalAreaCover.text = localuser.totalArea.toString()
        }
    }

    private fun addRecommendedDashboardItems(localuser: User) {
        val arr = arrayListOf<Int>()
        if (localuser.presentAction.isNullOrEmpty()) {
            arr.add(localuser.remainingAction[0].toInt())
            arr.add(localuser.remainingAction[1].toInt())
            arr.add(localuser.remainingAction[2].toInt())
        } else {
            localuser?.presentAction.forEach {
                arr.add(it.toInt())
            }
            var i = 0
            while (arr.size <= 3)
                arr.add(localuser.remainingAction[i++].toInt())
        }

        setValueTasks(arr[0], arr[1], arr[2])

    }

    private fun setValueTasks(taskId1: Int, taskId2: Int, taskId3: Int) {
        initializeAllTasks()
        addNavigation(taskId1, taskId2, taskId3)
        binding.apply {
            this.targetTextView1.text = actionsList[taskId1 - 1].title
            this.targetTextView2.text = actionsList[taskId2 - 1].title
            this.targetTextView3.text = actionsList[taskId3 - 1].title
            this.targetBlackImageView.setImageResource(actionsList[taskId1 - 1].background)
            this.targetBlackImageView2.setImageResource(actionsList[taskId2 - 1].background)
            this.targetBlackImageView3.setImageResource(actionsList[taskId3 - 1].background)
        }
    }

    private fun addNavigation(taskId1: Int, taskId2: Int, taskId3: Int) {
        for(i in 1..3){
            val card = returnImageId(i.toString())
            card.setOnClickListener {
            var status = "remaining"
                if(i==1) {
                    status = returnStatus(taskId1.toString())
                    if(status=="remaining")
                        addTask(taskId1.toString())
                    navigateToTask(taskId1, status)
                }
                if(i==2) {
                    status = returnStatus(taskId2.toString())
                    if(status=="remaining")
                        addTask(taskId2.toString())
                    navigateToTask(taskId2, status)
                }
                if(i==3) {
                    status = returnStatus(taskId3.toString())
                    if(status=="remaining")
                        addTask(taskId3.toString())
                    navigateToTask(taskId3, status)
                }
            }
        }
    }

    private fun addTask(taskId: String) {
        Toast.makeText(context, "Adding task", Toast.LENGTH_SHORT).show()
        val uid = FirebaseAuth.getInstance().uid
        val changePresentList =
            localuser.presentAction.toMutableList()
        val changeRemainingList =
            localuser.remainingAction.toMutableList()
        if (changeRemainingList.indexOf(taskId) != -1)
            changeRemainingList.removeAt(changeRemainingList.indexOf(taskId))
        if (changePresentList.indexOf(taskId) == -1)
            changePresentList.add(taskId)
        FirebaseDatabase.getInstance().getReference("/Users/$uid/presentAction")
            .setValue(changePresentList)
        FirebaseDatabase.getInstance().getReference("/Users/$uid/remainingAction")
            .setValue(changeRemainingList)
            .addOnSuccessListener {
                Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show()
            }
    }

    private fun returnStatus(taskId1: String): String
    {
        if(localuser.presentAction.contains(taskId1))
            return "present"
        return "remaining"
    }

    private fun returnImageId(cardId: String): ConstraintLayout {
        return when(cardId) {
            "1" -> binding.recommendedCardView
            "2" -> binding.recommendedCardView2
            "3" -> binding.recommendedCardView3
            else -> binding.recommendedCardView
        }
    }


    private fun navigateToTask(i: Int, status: String) {
        when(actionsList[i - 1].category){
            TaskFragment.tree -> binding.root.findNavController().navigate(
                DashboardDirections.actionDashboardFragmentToTreesPlanted(
                    actionsList[i - 1].number,
                    status,
                    i.toString()
                )
            )
            TaskFragment.seminar -> binding.root.findNavController()
                .navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
            TaskFragment.guide -> binding.root.findNavController().navigate(
                DashboardDirections.actionDashboardFragmentToGuideTask(
                    i.toString()
                )
            )
            TaskFragment.report -> binding.root.findNavController()
                .navigate(DashboardDirections.actionDashboardFragmentToSendReportFragment())
            TaskFragment.refer -> binding.root.findNavController()
                .navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
            TaskFragment.recycle -> binding.root.findNavController().navigate(
                DashboardDirections.actionDashboardFragmentToRecycleTask(
                    i.toString()
                )
            )
            TaskFragment.purchase -> binding.root.findNavController()
                .navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
            TaskFragment.feed -> binding.root.findNavController()
                .navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
        }
    }

    private fun circularloader(data: Float, max: Float, circularProgressBar: CircularProgressBar) {
        circularProgressBar.apply {
            setProgressWithAnimation(data, 3000) // =1s
            progressMax = max
            roundBorder = true
            startAngle = 180f
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
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

}

