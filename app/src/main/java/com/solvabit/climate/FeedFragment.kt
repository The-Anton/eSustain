package com.solvabit.climate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.solvabit.climate.database.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.card_post_view.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import timber.log.Timber.d


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
        fetchPostData()
        return v
    }

    private fun fetchPostData() {
        var ref = FirebaseDatabase.getInstance().getReference("/PostData")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter= GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    Log.v("data",it.toString())
                    val post = it.getValue(Post::class.java)
                    if (post!=null){
                        adapter.add(postItem(post))}
                }
                post_recycler_view.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}
class postItem(private val post: Post): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.card_post_view
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.post_main_text.text=post.post_text
        Picasso.get().load(post.post_image).into(viewHolder.itemView.post_main_image)
        val uid = post.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                viewHolder.itemView.username.text = user?.username
                if (user?.imageUrl!=null){
                    Picasso.get().load(user.imageUrl).into(viewHolder.itemView.user_image)
                }

            }

        })
    }
}