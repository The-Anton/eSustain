package com.solvabit.climate.dataModel

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.solvabit.climate.R
import com.solvabit.climate.database.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.card_issue_post.view.*
import kotlinx.android.synthetic.main.card_post_view.view.category
import kotlinx.android.synthetic.main.card_post_view.view.post_main_image
import java.text.SimpleDateFormat
import java.util.*


class IssuePost(private val post: Post) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        initializePostData(viewHolder)


    }

    override fun getLayout(): Int {
        return R.layout.card_issue_post
    }

    private fun initializePostData(viewHolder: ViewHolder) {
        viewHolder.itemView.issue_post_main_text.text = post.post_text
        if (post.post_image.isNotEmpty())
            Picasso.get().load(post.post_image).into(viewHolder.itemView.post_main_image)
        viewHolder.itemView.category.text = post.category
        val time = post.time.toLong()
        val sfd = SimpleDateFormat("dd-MM-yyyy")
        viewHolder.itemView.issue_time.text = sfd.format(Date(time))
        val uid = post.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                viewHolder.itemView.issue_username.text = user?.username
                if (user?.imageUrl != null) {
                    Picasso.get().load(user.imageUrl).into(viewHolder.itemView.issue_user_image)
                }

            }
        })
    }


}