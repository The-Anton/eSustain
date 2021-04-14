package com.solvabit.climate.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.TreesPlantedFragmentBinding
import com.solvabit.climate.fragment.PlantTreesFragments.PlantNewTreeDialog
import com.solvabit.climate.fragment.PlantTreesFragments.ShareAchievementToPostDialog
import com.solvabit.climate.viewModel.TreesPlantedViewModel
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.single_tree.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class TreesPlanted : Fragment() {

    private val uid = FirebaseAuth.getInstance().uid
    private var treesPlanted: Int = 0
    private var targetTrees: Int = 0
    private var status: String = "remaining"
    private lateinit var allPlantsList: MutableList<Trees>
    private var taskId = "1"
    private var localTreesPlanted = 0
    private val localUser = Dashboard.localuser

    private lateinit var viewModel: TreesPlantedViewModel
    private lateinit var binding: TreesPlantedFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.trees_planted_fragment, container, false
        )

        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        val args = TreesPlantedArgs.fromBundle(requireArguments())
        targetTrees = args.targetTrees
        status = args.status
        taskId = args.taskId

        fetchCurrentUser()

        fetchTrees()

        circularProgress(localTreesPlanted, targetTrees)

        binding.addPicFiveTrees.setOnClickListener {
            val dialog = PlantNewTreeDialog(targetTrees, treesPlanted, taskId)
            dialog.show(childFragmentManager, "TreesPlanted")
        }

        binding.linearLayoutShareTreesPlanted.setOnClickListener {
            shareAchievementToPost()
        }

        return binding.root
    }

    private fun shareAchievementToPost() {
        val dialog = ShareAchievementToPostDialog(allPlantsList)
        dialog.show(childFragmentManager, "ShareAchievement")
    }


    private fun fetchTrees() {
        binding.numberofTreesPlanted.text = "00"
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid/AllTasks/$taskId")

        val adapter = GroupAdapter<ViewHolder>()
        binding.allTreesRecyclerView.adapter = adapter
        allPlantsList = mutableListOf()

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val tree = p0.getValue(Trees::class.java)
                adapter.add(AddRecycleItemTrees(tree!!))
                allPlantsList.add(tree)
                localTreesPlanted = allPlantsList.size
                if (localTreesPlanted >= targetTrees) {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.linearLayoutShareTreesPlanted.visibility = View.VISIBLE
                    binding.addPicFiveTrees.visibility = View.GONE
                    if (localUser.remainingAction.contains(taskId) || localUser.presentAction.contains(
                            taskId
                        )
                    ) {
                        Timber.i("Inside remaining")
                        val changePresentList =
                            Dashboard.localuser.presentAction.toMutableList()
                        val changeRemainingList =
                            Dashboard.localuser.remainingAction.toMutableList()
                        val changeCompletedList =
                            Dashboard.localuser.completedAction.toMutableList()
                        if (changePresentList.indexOf(taskId) != -1)
                            changePresentList.removeAt(changePresentList.indexOf(taskId))
                        if (changeRemainingList.indexOf(taskId) != -1) {
                            Timber.i("Removing remaining list")
                            changeRemainingList.removeAt(changeRemainingList.indexOf(taskId))
                        }
                        if (changeCompletedList.indexOf(taskId) == -1)
                            changeCompletedList.add(taskId)
                        FirebaseDatabase.getInstance().getReference("/Users/$uid/presentAction")
                            .setValue(changePresentList)
                        FirebaseDatabase.getInstance().getReference("/Users/$uid/remainingAction")
                            .setValue(changeRemainingList)
                        FirebaseDatabase.getInstance().getReference("/Users/$uid/completedAction")
                            .setValue(changeCompletedList)
                    }
                }
                binding.numberofTreesPlanted.text = "0" + localTreesPlanted + "/ 0" + targetTrees
            }
        })
    }

    private fun fetchCurrentUser() {
        treesPlanted = localUser.treesPlanted ?: 0
        binding.achievementNameTextView.text = localUser.username
        Picasso.get().load(localUser.imageUrl).into(binding.userprofileImageView)
    }

    private fun circularProgress(progress: Int, maxProgress: Int) {
        val circularProgressBar = binding.circularProgressBar
        circularProgressBar.apply {
            setProgressWithAnimation(progress.toFloat(), 4000) // =1s
            // Set Progress Max
            progressMax = maxProgress.toFloat()
            // Set ProgressBar Color
            progressBarColor = Color.BLACK
            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.WHITE
            progressBarWidth = 4f // in DP
            // Other
            roundBorder = true
            startAngle = 0f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TreesPlantedViewModel::class.java)
    }

}


class AddRecycleItemTrees(private val tree: Trees) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.single_tree
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val sfd = SimpleDateFormat.getDateTimeInstance()
        viewHolder.itemView.plant_date.text = sfd.format(Date(tree.timestamp))
        Picasso.get().load(tree.treeImage).into(viewHolder.itemView.single_tree_image)
    }
}


@Parcelize
class Trees(val treeImage: String, val timestamp: Long) :
    Parcelable {
    constructor() : this("", -1)
}