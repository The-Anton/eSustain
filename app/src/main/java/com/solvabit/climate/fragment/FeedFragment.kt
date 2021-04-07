package com.solvabit.climate

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.database.User
import com.solvabit.climate.databinding.FeedFragmentBinding
import com.solvabit.climate.dialog.JoinGroupConfirmDialog
import com.solvabit.climate.fragment.Dashboard
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.card_post_view.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment() {

    private lateinit var binding: FeedFragmentBinding
    private val localUser = Dashboard.localuser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.feed_fragment, container, false
        )

        binding.createNewPost.setOnClickListener {
            binding.root.findNavController()
                    .navigate(FeedFragmentDirections.actionFeedFragmentToCreatePostFragment())
        }

        binding.allMessages.setOnClickListener {
            binding.root.findNavController()
                    .navigate(FeedFragmentDirections.actionFeedFragmentToAllChatsFragment())
        }
        initializeShareThoughtFeed()

        fetchPostData(requireContext().applicationContext)


        return binding.root
    }

    private fun initializeShareThoughtFeed() {
        Picasso.get().load(localUser.imageUrl).into(binding.userprofileImageViewFeed)
    }


   /* private fun popFeedMenu() {
        binding.feedMenuBtn.setOnClickListener {
            context?.let { context ->
                val popupMenu = android.widget.PopupMenu(context, binding.feedMenuBtn)
                popupMenu.menuInflater.inflate(R.menu.feed_top_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    if (it.itemId == R.id.all_chats_menu) {
                        binding.root.findNavController()
                                .navigate(FeedFragmentDirections.actionFeedFragmentToAllChatsFragment())
                    }
                    true
                }

                popupMenu.show()
            }
        }
    } */


    private fun fetchPostData(context: Context) {
        val adapter = GroupAdapter<ViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/PostData")
        binding.postRecyclerView.adapter = adapter

        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val post = p0.getValue(Post::class.java)
                if (post != null)
                    adapter.add(PostItem(post, context))
            }
        })
    }

}


class PostItem(private val post: Post, val context: Context) : Item<ViewHolder>() {

    private lateinit var database: DatabaseReference
    override fun getLayout(): Int {
        return R.layout.card_post_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        initializePostData(viewHolder)

        enableInterestedButton(viewHolder)

        popPostMenu(viewHolder)

        enableLikeButton(viewHolder)
    }

    private fun enableInterestedButton(viewHolder: ViewHolder) {
        viewHolder.itemView.interested_button.setOnClickListener {
            val dialog = JoinGroupConfirmDialog(post)
            Timber.i("Interested button clicked")
            dialog.show((context as AppCompatActivity).supportFragmentManager, "JoinGroupDialog")
        }
    }

    private fun initializePostData(viewHolder: ViewHolder) {
        viewHolder.itemView.post_main_text.text = post.post_text
        if (post.post_image.isNotEmpty())
            Picasso.get().load(post.post_image).into(viewHolder.itemView.post_main_image)
        viewHolder.itemView.category.text = post.category
        database = Firebase.database.reference
        val time = post.time.toLong()
        val sfd = SimpleDateFormat("dd-MM-yyyy")
        viewHolder.itemView.time.text = sfd.format(Date(time))
        val uid = post.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        if(post.category!="Issue")
            viewHolder.itemView.interested_button.visibility = View.GONE

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                viewHolder.itemView.username.text = user?.username
                if (user?.imageUrl != null) {
                    Picasso.get().load(user.imageUrl).into(viewHolder.itemView.user_image)
                }

            }
        })
    }

    private fun enableLikeButton(viewHolder: ViewHolder) {

        val likeref = FirebaseDatabase.getInstance().getReference("/PostData/${post.key}/likes/")
        val currentUserUid = FirebaseAuth.getInstance().uid

        likeref.get().addOnSuccessListener { snapshot ->
            var count = 0
            snapshot.children.forEach {
                if (it.value == true) {
                    count++
                }
            }
            viewHolder.itemView.like_count.text = count.toString()
            if (snapshot.hasChild(currentUserUid.toString())) {
                if (snapshot.child(currentUserUid.toString()).value == true) {
                    viewHolder.itemView.like_icon.setColorFilter(Color.BLUE)
                    viewHolder.itemView.like_text.setTextColor(Color.BLUE)
                }
            }
        }
        viewHolder.itemView.like_button.setOnClickListener {

            likeref.get().addOnSuccessListener { snapshot ->
                if (snapshot.hasChild(currentUserUid.toString())) {
                    val bool = snapshot.child(currentUserUid.toString()).value
                    if (bool == true) {
                        viewHolder.itemView.like_icon.setColorFilter(Color.BLACK)
                        viewHolder.itemView.like_text.setTextColor(Color.BLACK)
                        viewHolder.itemView.like_count.text =
                                (viewHolder.itemView.like_count.text.toString().toInt() - 1).toString()
                        likeref.child(currentUserUid.toString()).setValue(false)
                    } else {
                        viewHolder.itemView.like_icon.setColorFilter(Color.BLUE)
                        viewHolder.itemView.like_text.setTextColor(Color.BLUE)
                        viewHolder.itemView.like_count.text =
                                (viewHolder.itemView.like_count.text.toString().toInt() + 1).toString()
                        likeref.child(currentUserUid.toString()).setValue(true)
                    }
                } else {
                    viewHolder.itemView.like_icon.setColorFilter(Color.BLUE)
                    viewHolder.itemView.like_text.setTextColor(Color.BLUE)
                    viewHolder.itemView.like_count.text =
                            (viewHolder.itemView.like_count.text.toString().toInt() + 1).toString()
                    likeref.child(currentUserUid.toString()).setValue(true)
                }

            }

        }

    }

    private fun popPostMenu(viewHolder: ViewHolder) {
        viewHolder.itemView.post_more.setOnClickListener {
            val popupMenu = PopupMenu(context, viewHolder.itemView.post_more)
            popupMenu.menuInflater.inflate(R.menu.post_more_options, popupMenu.menu)
            popupMenu.show()
        }
    }

}
