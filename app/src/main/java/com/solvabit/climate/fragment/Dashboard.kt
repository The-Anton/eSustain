package com.solvabit.climate.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.SingleAction
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.DashboardFragmentBinding
import com.solvabit.climate.dialog.AqiDialog
import com.solvabit.climate.dialog.ForestDialog
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
    private lateinit var v: View

    companion object {
        fun newInstance() = Dashboard()
        var localuser= User()

    }

    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
                    Timber.i("${user}")
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
            Repository(dao,uid).fetchUpdates {
                Log.v("Dashboard", localuser.toString())
                addRecommendedDashboardItems()
                addDataToDashboard()
            }
        }


        return  binding.root
    }


    override fun onStart() {
        super.onStart()
        addRecommendedDashboardItems()

    }

    override fun onResume() {
        super.onResume()
        addRecommendedDashboardItems()

    }
  /*
    private fun popMenu() {


        binding.airMore.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.airMore)
                popupMenu.menuInflater.inflate(R.menu.cards_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.Refresh) {
                        Timber.i("SignOut can be pressed")
                    } else if (it.itemId == R.id.more) {
                        Timber.i("Nothing happened")
                    }
                    true
                }

                popupMenu.show()
            }
        }

    }  */

    private fun addDataToDashboard() {
        binding.normalizedScoreData.text = localuser.normalizedScore.toString()
        binding.airQualityData.text = localuser.aqi.toString()
        binding.forestDensityData.text = localuser.forestDensity.toString()
        val normalizedScore: Float = localuser.normalizedScore?.toFloat() ?: 0f

        if (localuser.normalizedScore!=null){
            if(localuser.normalizedScore!! <= 500 ){
                binding.statusText.text = " Your are in Critical Condition!"
                binding.linearLayout3.setBackgroundResource(R.drawable.red_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_block_24)

                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_block_24)
            }else if(localuser.normalizedScore!! <= 700 ){
                binding.statusText.text = "  Your are on border line !"
                binding.linearLayout3.setBackgroundResource(R.drawable.orange_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_warning_24)
            }else{
                binding.statusText.text = "  Congratulation! You are good"
                binding.linearLayout3.setBackgroundResource(R.drawable.home_rounded_button)
                binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
            }
        }else{
            binding.statusText.text = "  Congratulation! You are good"
            binding.imageView6.setBackgroundResource(R.drawable.ic_baseline_check_circle_24)
        }

        binding.circularProgressBar.progress = normalizedScore/10
        circularloader(localuser.aqi?.toFloat() ?: 0f, 500f, binding.circularProgressBarAirQuality)
        initializeAirData()
        initializeForestData()
        addRecommendedDashboardItems()
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
        circularloader(localuser.forestDensity?.toFloat() ?: 0f, 10f, binding.circularProgressBarForestDensity)
        binding.apply {
            this.actualForestCover.text = localuser.actualForest.toString()
            this.openForestCover.text = localuser.openForest.toString()
            this.noForestCover.text = localuser.noForest.toString()
            this.totalAreaCover.text = localuser.totalArea.toString()
        }
    }

    private fun addRecommendedDashboardItems(){

        val adapter = GroupAdapter<ViewHolder>()
        binding.recommendedRecyclerView.adapter = adapter

        localuser.presentAction.forEach{
            if(it!="0") {
                adapter.add(AddRecycleItemRecommended(it.toInt(), "present", binding))
            }
        }

        localuser.remainingAction.forEach {
            if(it!="0") {
                adapter.add(AddRecycleItemRecommended(it.toInt(), "remaining", binding))
            }
        }

        localuser.completedAction.forEach{
            if(it!="0") {
                adapter.add(AddRecycleItemRecommended(it.toInt(), "completed", binding))
            }
        }

    }


    private fun circularloader(data: Float, max:Float, circularProgressBar : CircularProgressBar){
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
        // TODO: Use the ViewModel
    }

}

//Create instances of all actions
private val action1 = SingleAction(1, "Plant five Trees", "Let's begin a new journey!", 5)
private val action2 = SingleAction(2, "Plant ten Trees", "Let's begin a new journey!", 10)
private val action3 = SingleAction(3, "Use Public Transport", "Let's begin a new journey!", 15)
private val action4 = SingleAction(4, "Refer a friend", "Let's begin a new journey!", 5)


class AddRecycleItemRecommended(val a: Int, val status: String, val binding: DashboardFragmentBinding): Item<ViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.recommended_cards
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(a==0)
            return
        var action = SingleAction()
        action = when(a)
        {
            1->action1
            2->action2
            3->action3
            4->action4
            else-> action4
        }
        viewHolder.itemView.target_textView.text = action.topic
        viewHolder.itemView.target_textView_subtext.text = action.subtopic
        viewHolder.itemView.background_play_pause.setOnClickListener {
            when(a){
                1-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToTreesPlanted(5, status))
                2-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToTreesPlanted(10, status))
                3-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
                4-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
            }
        }
    }

}