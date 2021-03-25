package com.solvabit.climate.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.solvabit.climate.R
import com.solvabit.climate.database.User
import com.solvabit.climate.databinding.TreesPlantedFragmentBinding
import com.solvabit.climate.viewModel.TreesPlantedViewModel
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_trees_planted.*
import kotlinx.android.synthetic.main.single_tree.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TreesPlanted : Fragment() {

    private val uid = FirebaseAuth.getInstance().uid
    var treesPlanted : Int = 0

    companion object {
        fun newInstance() = TreesPlanted()
    }

    private lateinit var viewModel: TreesPlantedViewModel
    private lateinit var binding: TreesPlantedFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.trees_planted_fragment, container, false
        )

        fetchCurrentUser()

        fetchTrees()

        circularProgress()

        binding.addPicFiveTrees.setOnClickListener {
            uploadImageDataToDevice()
        }

        return binding.root
    }


    private fun uploadImageDataToDevice()
    {
        binding.addPicFiveTrees.startAnimation()
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)

    }

    private var selectedPhotoUri : Uri?= null

    private var imageUrl: String ? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode== Activity.RESULT_OK && data!= null)
        {
            selectedPhotoUri = data.data

            val deepColor = Color.parseColor("#27E1EF")
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.blue_tick)
            binding.addPicFiveTrees.doneLoadingAnimation(deepColor, largeIcon)

            binding.postAddPicFiveTrees.visibility = View.VISIBLE
            binding.postAddPicFiveTrees.setOnClickListener {
                binding.postAddPicFiveTrees.startAnimation()
                uploadPhotoToFirebase()
            }

        }
        else
        {
            binding.addPicFiveTrees.revertAnimation()
            return
        }
    }

    private fun uploadPhotoToFirebase() {
        if (selectedPhotoUri == null){
            Snackbar.make(binding.root, "Hello Snackbar", Snackbar.LENGTH_SHORT).show()
            binding.postAddPicFiveTrees.revertAnimation()
            binding.postAddPicFiveTrees.visibility = View.INVISIBLE
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images-send-by-users/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        Timber.i("image downloaded url : $imageUrl")

                        val timestamp = System.currentTimeMillis()
                        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid/fiveTrees/$timestamp")
                        treesPlanted += 1
                        val tree = Trees(
                                imageUrl.toString(),
                                System.currentTimeMillis()
                        )

                        ref.setValue(tree)
                                .addOnSuccessListener {
                                    val handler = Handler()
                                    handler.postDelayed({
                                        binding.addPicFiveTrees.revertAnimation()
                                        binding.postAddPicFiveTrees.revertAnimation()
                                        fetchTrees()
                                        FirebaseDatabase.getInstance().getReference("/Users/$uid").child("treesPlanted").setValue(treesPlanted)
                                        binding.addPicFiveTrees.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rounded_button_login_register, 0, 0, 0);
                                        binding.postAddPicFiveTrees.visibility = View.INVISIBLE
                                    },1000)
                                    selectedPhotoUri = null
                                }
                                .addOnFailureListener {
                                    binding.addPicFiveTrees.revertAnimation()
                                    binding.postAddPicFiveTrees.revertAnimation()
                                    binding.postAddPicFiveTrees.visibility = View.INVISIBLE
                                }
                    }
                }
                .addOnFailureListener{
                    binding.addPicFiveTrees.revertAnimation()
                    binding.postAddPicFiveTrees.revertAnimation()
                    binding.postAddPicFiveTrees.visibility = View.INVISIBLE
                }
    }

    private fun fetchTrees(){
        binding.numberofTreesPlanted.text = "00"
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid/fiveTrees")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            var i = 0;
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                binding.allTreesRecyclerView.adapter = adapter
                p0.children.forEach{
                    val tree = it.getValue(Trees::class.java)
                    adapter.add(AddRecycleItemTrees(tree!!))
                    i += 1;
                    if(i>=5)
                        binding.linearLayout.visibility = View.VISIBLE
                    binding.numberofTreesPlanted.text = "0" + i.toString()
                }
            }

        })


    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)
                treesPlanted = currentUser?.treesPlanted!!
                binding.achievementNameTextView.text = currentUser?.username
                Picasso.get().load(currentUser.imageUrl).into(binding.userprofileImageView)
            }

        })
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
        // TODO: Use the ViewModel
    }

}


class AddRecycleItemTrees(private val tree: Trees): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.single_tree
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        viewHolder.itemView.plant_date.text = sfd.format(Date(tree.timestamp))
        Picasso.get().load(tree.treeImage).into(viewHolder.itemView.single_tree_image)
    }
}




@Parcelize
class Trees(val treeImage: String ,val timestamp: Long):
        Parcelable {
    constructor(): this("",-1)
}