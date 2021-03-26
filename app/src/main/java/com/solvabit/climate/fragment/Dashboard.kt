package com.solvabit.climate.fragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.SingleAction
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.DashboardFragmentBinding
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

        popMenu()



        return  binding.root
    }

    private fun popMenu() {
        binding.forestMore.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.forestMore)
                popupMenu.menuInflater.inflate(R.menu.cards_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.Refresh) {
                        Timber.i("SignOut pressed")
                    } else if (it.itemId == R.id.more) {
                        Timber.i( "Nothing happened")
                    }
                    true
                }

                popupMenu.show()
            }
        }

        binding.airMore.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.airMore)
                popupMenu.menuInflater.inflate(R.menu.cards_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.Refresh) {
                        Timber.i("Signout pressed")
                    } else if (it.itemId == R.id.more) {
                        Timber.i("Nothing happened")
                    }
                    true
                }

                popupMenu.show()
            }
        }

    }

    private fun addDataToDashboard() {
        binding.normalizedScoreData.text = localuser.normalizedScore.toString()
        binding.airQualityData.text = localuser.aqi.toString()
        binding.forestDensityData.text = localuser.forestDensity.toString()
        val normalizedScore: Float = localuser.normalizedScore?.toFloat() ?: 0f
        binding.circularProgressBar.progress = normalizedScore/10
        circularloader(localuser.aqi?.toFloat() ?: 0f, 500f, binding.circularProgressBarAirQuality)
        initializeAirData()
        initializeForestData()
        addRecommendedDashboardItems()
    }

    private fun initializeAirData() {
        val co:Double = localuser.co ?: 0.toDouble()
        val so2:Double = localuser.so2 ?: 0.toDouble()
        val o3:Double = localuser.o3 ?: 0.toDouble()
        val no2: Double = localuser.no2 ?: 0.toDouble()
        val totalSum = co + so2 + o3 + no2
        val coPercentage = ( co.toFloat() /totalSum.toFloat() ) * 100
        val so2Percentage = ( so2.toFloat() /totalSum.toFloat() ) * 100
        val o3Percentage = ( o3.toFloat() /totalSum.toFloat() ) * 100
        val no2Percentage = ( no2.toFloat() / totalSum.toFloat()) * 100
        binding.apply {
            this.coAirQualityProgressView.progress = coPercentage
            this.so2AirQualityProgressbar.progress = so2Percentage
            this.o3AirQualityProgressView.progress = o3Percentage
            this.no2AirQualityProgressView.progress = no2Percentage
        }
    }

    private fun initializeForestData() {
        circularloader(localuser.forestDensity?.toFloat() ?: 0f, 10f, binding.circularProgressBarForestDensity)
        val actualForest:Int = localuser.actualForest ?: 0
        val openForest:Int = localuser.openForest ?: 0
        val noForest:Int = localuser.noForest ?: 0
        val totalSum = actualForest + openForest + noForest
        val actualForestPercentage = ( actualForest.toFloat() /totalSum.toFloat() ) * 100
        val openForestPercentage = ( openForest.toFloat() /totalSum.toFloat() ) * 100
        val noForestPercentage = ( noForest.toFloat() /totalSum.toFloat() ) * 100
        binding.apply {
            this.actualForestCoverProgressView.progress = actualForestPercentage
            this.openForestProgessView.progress = openForestPercentage
            this.noForestProgressView.progress = noForestPercentage
        }
    }

    private fun addRecommendedDashboardItems(){

        val adapter = GroupAdapter<ViewHolder>()
        binding.recommendedRecyclerView.adapter = adapter

        localuser.presentAction.forEach{
            if(it!="0") {
//                adapter.add(AddRecycleItemRecommended(it.toInt(), "present"))
            }
        }

        localuser.remainingAction.forEach {
            if(it!="0") {
                adapter.add(AddRecycleItemRecommended(it.toInt(), "remaining", binding))
            }
        }

        localuser.completedAction.forEach{
            if(it!="0") {
//                adapter.add(AddRecycleItemRecommended(it.toInt(), "completed"))
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


class AddRecycleItemRecommended(val a: Int, val action: String, val binding: DashboardFragmentBinding): Item<ViewHolder>(){

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
                1-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToTreesPlanted(5))
                2-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToTreesPlanted(10))
                3-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
                4-> binding.root.findNavController().navigate(DashboardDirections.actionDashboardFragmentToSendReferral())
            }
        }
    }

}