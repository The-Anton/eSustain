package com.solvabit.climate

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.solvabit.climate.activity.CreatePost
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.database.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_trees_planted.*
import kotlinx.android.synthetic.main.card_post_view.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_fragment.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment() {
    private lateinit var v:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.feed_fragment, container, false)

        v.findViewById<LinearLayout>(R.id.create_new_post).setOnClickListener {
            activity?.let{
                val intent = Intent (it, CreatePost::class.java)
                it.startActivity(intent)
            }
        }
        fetchPostData(requireContext())
        return v
    }

    private fun fetchPostData(context: Context) {
        val adapter = GroupAdapter<ViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/PostData")
                v.post_recycler_view.adapter = adapter

                ref.addChildEventListener(object: ChildEventListener{

                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                    override fun onChildRemoved(p0: DataSnapshot) {}

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val post = p0.getValue(Post::class.java)
                        Timber.i("Again - ${post}")
                        if(post!=null)
                            adapter.add(postItem(post, context))
                    }
                })

//                    val post: Post = p0.getValue(Post::class.java)!!
//                    Timber.i("Post - ${it.value}")
//                    Timber.i("Again - ${post}")
//                    if(post!=null)
//                        adapter.add(postItem(post, context))
    }

}
class postItem(private val post: Post,val context: Context): Item<ViewHolder>() {

    private lateinit var database: DatabaseReference
    override fun getLayout(): Int {
        return R.layout.card_post_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.post_main_text.text = post.post_text
        Picasso.get().load(post.post_image).into(viewHolder.itemView.post_main_image)
        viewHolder.itemView.category.text = post.category
        database = Firebase.database.reference
        val likes = post.likes.toMutableList()
        viewHolder.itemView.like_count.text = (likes.size-1).toString()
        val time = post.time.toLong()
        val key = Long.MAX_VALUE - time
        val sfd = SimpleDateFormat("dd-MM-yyyy")
        viewHolder.itemView.time.text = sfd.format(Date(time))
        val uid = post.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.keepSynced(true)

        val currentUserUid = FirebaseAuth.getInstance().uid
        for (i in likes) {
            if (i == currentUserUid) {
                viewHolder.itemView.like_icon.setColorFilter(Color.BLUE)
                viewHolder.itemView.like_text.setTextColor(Color.BLUE)
            }
        }
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
        viewHolder.itemView.post_more.setOnClickListener {

            val popupMenu = PopupMenu(context, viewHolder.itemView.post_more)
            popupMenu.menuInflater.inflate(R.menu.post_more_options, popupMenu.menu)

            popupMenu.show()
        }
        viewHolder.itemView.like_button.setOnClickListener {

            if (currentUserUid !in likes){
                val updatedList:MutableList<String> = (likes+currentUserUid) as MutableList<String>
                //  val updatedPost = Post(post.post_text,post.post_image,post.uid,post.time,post.category, updatedList,post.interested)
                val ref = FirebaseDatabase.getInstance().getReference("/PostData/$key")
                ref.updateChildren(mapOf("likes" to updatedList))
            }
            if (currentUserUid in likes) {
                    // dislike the button
                    val listKey = likes.indexOf(currentUserUid)
                    val updatedList = likes.remove(currentUserUid)
                    val ref = FirebaseDatabase.getInstance().getReference("/PostData/$key/likes/$listKey")
                    ref.removeValue()
            }
        }
    }
}



