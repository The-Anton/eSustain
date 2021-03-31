package com.solvabit.climate.fragment.ChatFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.databinding.DashboardFragmentBinding
import com.solvabit.climate.databinding.FragmentAllChatsBinding
import com.solvabit.climate.fragment.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.card_all_chats.view.*
import kotlinx.android.synthetic.main.recommended_cards.view.*


class AllChatsFragment : Fragment() {

    private lateinit var binding: FragmentAllChatsBinding
    private val localUser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_all_chats, container, false
        )

        val adapter = GroupAdapter<ViewHolder>()
        binding.allChatsRecyclerView.adapter = adapter

        localUser.interestedGroups.forEach {
            adapter.add(AddAllInterestedGroups(it, binding))
        }

        return binding.root
    }

}

class AddAllInterestedGroups(val post: Post, val binding: FragmentAllChatsBinding): Item<ViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.card_all_chats
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_user_row.text = post.key
        Picasso.get().load(post.post_image).into(viewHolder.itemView.profileimage_user_row)
    }

}