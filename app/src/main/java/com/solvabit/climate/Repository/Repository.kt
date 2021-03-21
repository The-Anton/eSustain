package com.solvabit.climate.Repository;

import android.util.Log
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.network.FirebaseService

public class Repository constructor(val dao: UserDao,val uid:String) {

    private  val firebaseService = FirebaseService(dao,uid)

     suspend fun getUser(myCallback: (result: User) -> Unit) {
        val userCount = dao.hasUser(uid)
        Log.v("Repository", "User count : ${userCount}")

        if( userCount == 1) {
            Log.v("Repository", "User is present in room database")
            val user = dao.getUserByUID(uid)
            Log.v("Repository", "User updated to ${user}")
            myCallback.invoke(user)
        }
        else{
            Log.v("Repository", "User is not present in room database... fetchUserData is called")

            val user = fetchUserData{
                result ->
                Log.v("Repository", "User updated to ${result}")
                dao.insert(result)
                Log.i("FirebaseService", "User INSERTED")
                myCallback.invoke(dao.getUserByUID(uid))
            }
            return user

        }

    }

     suspend fun fetchUserData(myCallback: (result:User)-> Unit){
        Log.v("Repository", "fetchUserData called")

            firebaseService.fetchUser { result ->
                myCallback.invoke(result)
            }

    }




}

