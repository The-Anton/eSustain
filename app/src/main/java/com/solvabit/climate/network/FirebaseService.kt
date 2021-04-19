package com.solvabit.climate.network

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.solvabit.climate.dataModel.Post
import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.registerLogin.ResetPassword.Companion.TAG
import com.solvabit.climate.utility.StatsUtility
import timber.log.Timber

class FirebaseService(var uid: String) {
    var database = FirebaseDatabase.getInstance()


    fun fetchUser(myCallback: (result: User) -> Unit) {
        val ref = database.getReference("Users/$uid")
        var user: User
        ref.get().addOnSuccessListener {
            user = it.getValue(User::class.java)!!
            Timber.tag("FirebaseService ").v("Fetched User From Firebase ${user}")

            myCallback.invoke(user)

        }.addOnFailureListener {
            Timber.tag("FirebaseService ").v("Error getting data")
        }
    }


    fun userStatus(myCallback: (result: String) -> Unit) {

        val refU = database.getReference("Users/$uid/updated")

        refU.get().addOnSuccessListener {

            var status = false
            if (it.value != null) {
                status = it.value as Boolean
            }
            Timber.tag("FirebaseService ").v("User is updated ->> ${status}")

            if (status == true) {
                myCallback.invoke("true")
            } else {
                myCallback.invoke("false")
            }
        }.addOnFailureListener {
            Timber.tag("FirebaseService ").v("Error getting data")
            myCallback.invoke("error")
        }
    }

    fun fetchContinuosUpdates(myCallback: (result: User) -> Unit) {

        val refU = database.getReference("Users/$uid")


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                Timber.tag("Repository ").v(user.toString())
                myCallback.invoke(user as User)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        refU.addValueEventListener(postListener)
    }

    fun fetchUpdates(myCallback: (result: User) -> Unit) {

        val refU = database.getReference("Users/$uid")

        refU.get().addOnSuccessListener {

            val user = it.getValue(User::class.java)!!

            Timber.tag("FirebaseService ").v("One time fetch update called")

            myCallback.invoke(user)

        }.addOnFailureListener {
            Timber.tag("FirebaseService ").v("Error getting data")
        }
    }

    fun fetchStats(dao: UserDao, myCallback: (result: Stats) -> Unit) {

        if (dao.hasUser(uid) == 1) {
            val user = dao.getUserByUID(uid)
            var state = user.state

            if (state != null) {
                state = state.toLowerCase()
            }
            val db = FirebaseFirestore.getInstance()
            val refU = state?.let {
                db.collection("forestCoverYearWiseComparision").document("india")
                    .collection("statesComparision").document(
                        it
                    )
            }

            if (refU != null) {
                refU.get().addOnSuccessListener {

                    val data = it.data
                    var stats = Stats()

                    Timber.tag("FirebaseService ").v("Stats data fetch update called")
                    Timber.tag("FirebaseService ").v(data.toString())

                    if (data != null) {
                        stats = StatsUtility().parseForestComparision(uid, data)
                    }

                    myCallback.invoke(stats)

                }.addOnFailureListener {
                    Timber.tag("FirebaseService ").v("Error getting stats data")
                }
            }
        }


    }


    fun writeUpdatesForTreesPlanted(user: User) {

        FirebaseDatabase.getInstance().getReference("/Users/$uid")
            .child("treesPlanted").setValue(user.treesPlanted)

    }

    fun fetchPostData(myCallback: (result: Post) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("/PostData").limitToFirst(10)

        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val post = p0.getValue(Post::class.java)
                if (post != null) {
                    myCallback.invoke(post)
                }
            }
        })
    }


    fun initializePostData(postUid: String, myCallback: (result: User) -> Unit) {

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$postUid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    myCallback.invoke(user)
                }
            }
        })
    }


}
