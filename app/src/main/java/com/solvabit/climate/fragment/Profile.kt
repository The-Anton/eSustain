package com.solvabit.climate.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.solvabit.climate.R
import com.solvabit.climate.Repository.Repository
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.database.UserDatabase
import com.solvabit.climate.databinding.ProfileFragmentBinding
import com.solvabit.climate.fragment.Dashboard.Companion.localuser
import com.solvabit.climate.registerLogin.RegistrationPage
import com.solvabit.climate.viewModel.ProfileViewModel
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.achievement_recycler_items.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class Profile : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment, container, false
        )

        Timber.i("User data: $localuser")
        binding.myUser = localuser
        Picasso.get().load(localuser.imageUrl).into(binding.userprofileImageView)
        binding.placeProfile.text = " " + localuser.city + ", " + localuser.state


        addItemsRecyclerView(localuser.completedAction)

        val instance = UserDatabase.getInstance(context?.applicationContext!!)
        val dao = instance.userDao()

        logoutMenu(dao)

        GlobalScope.launch {
            Repository(dao, localuser.uid).fetchUpdates {
                Log.v("Dashboard", localuser.toString())
                addItemsRecyclerView(localuser.completedAction)

            }
        }
        return binding.root
    }

    private fun logoutMenu(dao: UserDao) {
        binding.logoutPopup.setOnClickListener {
            context?.let {
                val popupMenu = PopupMenu(it, binding.logoutPopup)
                popupMenu.menuInflater.inflate(R.menu.nav_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.sign_out_menu) {
                        logoutUser(dao)
                    } else {
                        Timber.i("Nothing happened")
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        addItemsRecyclerView(localuser.completedAction)

    }

    override fun onResume() {
        super.onResume()
        addItemsRecyclerView(localuser.completedAction)

    }

    private fun addItemsRecyclerView(completed: List<String>) {
        val adapter = GroupAdapter<ViewHolder>()
        binding.recyclerViewProfileAchievements.adapter = adapter

        for (i in completed) {
            adapter.add(AddAchievementItems(i))
        }

        adapter.setOnItemClickListener { item, view ->
            val achievement = item as AddAchievementItems
            share(achievement.a.toInt())
        }
    }

    private fun share(a: Int) {
        val message = when (a) {
            0 -> "Hey!! I achieve Environment enthusiast status on joining Forest App. You can be the same and work for the sustainable future! "
            1 -> "Hey!! I planted 5 trees after joining Forest App. You can do the same and work for the sustainable future! "
            2 -> "Hey!! I planted 10 trees after joining Forest App. You can do the same and work for the sustainable future! "
            3 -> "Hey!! I helped spreading awareness regarding climate change after joining Forest App. You can do the same and work for the sustainable future! "
            4 -> "Hey!! I'm using Public Transport after joining Forest App. You can do the same and work for the sustainable future! "
            else -> "Hey!! I achieve Sustanaible Maker status on joining Forest App. You can be the same and work for the sustainable future! "
        }
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))

    }

    private fun logoutUser(dao: UserDao) {
        FirebaseAuth.getInstance().signOut()
        dao.delete(localuser)
        val intent = Intent(this.activity, RegistrationPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

class AddAchievementItems(val a: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.achievement_recycler_items
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        when (a) {
            "0" -> viewHolder.itemView.achievement_name_textView.text = "Environmental Enthusiast"
            "1" -> viewHolder.itemView.achievement_name_textView.text = "Planted 5 trees"
            "2" -> viewHolder.itemView.achievement_name_textView.text = "Planted 10 trees"
            "3" -> viewHolder.itemView.achievement_name_textView.text = "Transport Friendly"
            "4" -> viewHolder.itemView.achievement_name_textView.text = "Electric Savior"
            "5" -> viewHolder.itemView.achievement_name_textView.text = "Sustainable Maker"
        }
    }

}