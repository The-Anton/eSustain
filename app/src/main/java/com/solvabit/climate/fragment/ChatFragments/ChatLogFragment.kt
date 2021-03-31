package com.solvabit.climate.fragment.ChatFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solvabit.climate.R

class ChatLogFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }


//    private fun listenForMessages(){
//        val user = intent.getParcelableExtra<Users>(newmessageActivity.USER)
//        val fromId = FirebaseAuth.getInstance().uid
//        val toId = user.uid
//
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        ref.addChildEventListener(object: ChildEventListener{
//
//            override fun onCancelled(p0: DatabaseError) {}
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
//            override fun onChildRemoved(p0: DataSnapshot) {}
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val chatMessage = p0.getValue(ChatMessage::class.java)
//                val uid = FirebaseAuth.getInstance().uid
//
//                if(chatMessage != null) {
//                    Log.e("chat",chatMessage.text)
//
//                    val currentUser = messageActivity.currentUser
//
//                    if(chatMessage.fromId == uid && user.uid == chatMessage.toId )
//                    {
//                        if(chatMessage.text!="" && chatMessage.imageUrl!=""){
//
//                            adapter.add(sendImageInChatLog(chatMessage,currentUser!!))
//                            adapter.add(
//                                    ChatToItem(
//                                            chatMessage.text,
//                                            currentUser!!
//                                    )
//                            )
//                        }
//                        else if( chatMessage.text =="" && chatMessage.imageUrl!="") {
//                            adapter.add(sendImageInChatLog(chatMessage,currentUser!!))
//
//                        }
//                        else if(chatMessage.text!="" && chatMessage.imageUrl==""){
//                            adapter.add(ChatToItem(chatMessage.text, currentUser!!))
//                        }
//                        else{
//
//                        }
//
//                        recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
//
//                    }
//                    else if(chatMessage.toId == uid && user.uid == chatMessage.fromId){
//
//
//                        if(chatMessage.text!="" && chatMessage.imageUrl!=""){
//
//                            adapter.add(receiveImageInChatLog(chatMessage , user.imageUrl))
//                            adapter.add(ChatFromItem(chatMessage.text, user.imageUrl))
//                        }
//                        else if( chatMessage.text =="" && chatMessage.imageUrl!="") {
//                            adapter.add(receiveImageInChatLog(chatMessage , user.imageUrl))
//
//                        }
//                        else if(chatMessage.text!="" && chatMessage.imageUrl==""){
//                            adapter.add(ChatFromItem(chatMessage.text,user.imageUrl))
//                        }
//                        else{
//
//                        }
//                        recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
//
//                    }
//                    else{
//                    }
//
//                }
//            }
//        })
//    }


}