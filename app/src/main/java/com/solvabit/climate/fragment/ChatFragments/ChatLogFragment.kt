package com.solvabit.climate.fragment.ChatFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.solvabit.climate.R
import com.solvabit.climate.dataModel.ChatMessage
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.databinding.FragmentChatLogBinding
import com.solvabit.climate.fragment.Dashboard
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.card_from_chat_box.view.*
import kotlinx.android.synthetic.main.card_post_view.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChatLogFragment : Fragment() {

    private lateinit var binding: FragmentChatLogBinding
    private val adapter = GroupAdapter<ViewHolder>()
    private val localUser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_log, container, false)
        binding.recyclerViewChatLog.adapter = adapter

        val args = ChatLogFragmentArgs.fromBundle(requireArguments())
        val postData = args.postData

        binding.backArrowChatLog.setOnClickListener {
            binding.root.findNavController()
                    .navigate(ChatLogFragmentDirections.actionChatLogFragmentToAllChatsFragment())
        }

        binding.sendButtonChatLog.setOnClickListener {
            performSendMessage(postData)
        }

        listenForMessages(postData)

        initializePostData(postData)

        return binding.root
    }

    private fun listenForMessages(postData: Post) {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/allMessages/${postData.key}")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    if(chatMessage.fromId == uid)
                    {
                        adapter.add(AddToChatItem(chatMessage))
                    }
                    else
                    {
                        adapter.add(AddFromChatItem(chatMessage))
                    }
                    binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)

                }
            }
        })
    }

    private fun performSendMessage(postData: Post) {

        val text = binding.enterMessageChatLog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val imageUrl = localUser.imageUrl

        if (text.isEmpty()) return
        if (fromId == null) return

        val chatMessage =
                ChatMessage(
                        postData.key,
                        text,
                        fromId,
                        System.currentTimeMillis(),
                        imageUrl!!
                )

        val ref = FirebaseDatabase.getInstance().getReference("/allMessages/${postData.key}").push()
        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    binding.enterMessageChatLog.text.clear()
                    binding.recyclerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                }

    }

    private fun initializePostData(postData: Post) {
        binding.groupnameChatLog.text = postData.key
        Picasso.get().load(postData.post_image).into(binding.groupimageChatLog)
        val time = postData.time.toLong()
        val sfd = SimpleDateFormat("dd-MM-yyyy")
        binding.groupdateChatLog.text = sfd.format(Date(time))
    }

}


class AddFromChatItem(private val chatMessage: ChatMessage) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.card_from_chat_box
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_card_chat_box.text = chatMessage.text
        Picasso.get().load(chatMessage.imageUrl).into(viewHolder.itemView.imageView_card_chat_row)
    }
}

class AddToChatItem(private val chatMessage: ChatMessage) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.card_to_chat_box
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_card_chat_box.text = chatMessage.text
        Picasso.get().load(chatMessage.imageUrl).into(viewHolder.itemView.imageView_card_chat_row)
    }
}