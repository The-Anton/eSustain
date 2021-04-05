package com.solvabit.climate.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.databinding.TreesPlantedFragmentBinding
import com.solvabit.climate.fragment.PlantTreesFragments.PlantNewTreeDialog
import com.solvabit.climate.fragment.PlantTreesFragments.ShareAchievementToPostDialog
import com.solvabit.climate.viewModel.TreesPlantedViewModel
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_tree.view.*
import java.text.SimpleDateFormat
import java.util.*


class TreesPlanted : Fragment() {

    private val uid = FirebaseAuth.getInstance().uid
    private var treesPlanted: Int = 0
    private var targetTrees: Int = 0
    private var status: String = "remaining"
    private lateinit var allPlantsList: MutableList<Trees>

    private lateinit var viewModel: TreesPlantedViewModel
    private lateinit var binding: TreesPlantedFragmentBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.trees_planted_fragment, container, false
        )

        val args = TreesPlantedArgs.fromBundle(requireArguments())
        targetTrees = args.targetTrees
        status = args.status

        fetchCurrentUser()

        fetchTrees()

        circularProgress()

        bottomNavigation.visibility = View.GONE

        binding.addPicFiveTrees.setOnClickListener {
            val dialog = PlantNewTreeDialog(targetTrees, treesPlanted)
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
//        val bitmap = getBitmapFromView(binding.allTreesRecyclerView)
//        binding.tempImageView.setImageBitmap(bitmap)
    }



    private fun fetchTrees() {
        binding.numberofTreesPlanted.text = "00"
        val ref: DatabaseReference
        if (targetTrees == 5)
            ref = FirebaseDatabase.getInstance().getReference("/Users/$uid/fiveTrees")
        else
            ref = FirebaseDatabase.getInstance().getReference("/Users/$uid/tenTrees")

        var i = 0
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
                i += 1
                if (i >= targetTrees) {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.linearLayoutShareTreesPlanted.visibility = View.VISIBLE
                    binding.addPicFiveTrees.visibility = View.GONE
                    val changeRemainingList =
                            Dashboard.localuser.remainingAction.toMutableList()
                    val changeCompletedList =
                            Dashboard.localuser.completedAction.toMutableList()
                    if (targetTrees == 5 && status == "remaining") {
                        changeRemainingList.removeAt(0)
                        changeCompletedList.add("1")
                    } else if (targetTrees == 10 && status == "remaining") {
                        changeRemainingList.removeAt(1)
                        changeCompletedList.add("2")
                    }
                    FirebaseDatabase.getInstance().getReference("/Users/$uid/remainingAction")
                            .setValue(changeRemainingList)
                    FirebaseDatabase.getInstance().getReference("/Users/$uid/completedAction")
                            .setValue(changeCompletedList)
                }
                binding.numberofTreesPlanted.text = "0" + i.toString() + "/ 0" + targetTrees
            }
        })
    }

    private fun fetchCurrentUser() {
        treesPlanted = Dashboard.localuser.treesPlanted ?: 0
        binding.achievementNameTextView.text = Dashboard.localuser.username
        Picasso.get().load(Dashboard.localuser.imageUrl).into(binding.userprofileImageView)
    }

    private fun circularProgress() {
        val circularProgressBar = binding.circularProgressBar
        circularProgressBar.apply {
            setProgressWithAnimation(165f, 4000) // =1s
            // Set Progress Max
            progressMax = 200f
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