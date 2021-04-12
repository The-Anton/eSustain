package com.solvabit.climate.Repository;

import android.util.Log
import androidx.lifecycle.LiveData
import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.StatsDao
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.fragment.Dashboard.Companion.localuser
import com.solvabit.climate.network.FirebaseService

public class Repository constructor(val dao: UserDao,val uid:String) {

    private  val firebaseService = FirebaseService(uid)

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

    suspend fun getStats(statsDao:StatsDao, myCallback: (result: Stats) -> Unit) {
        val hasStats = statsDao.hasStats(uid)
        Log.v("Repository", "Stats count : ${hasStats}")

        if( hasStats == 1) {
            Log.v("Repository", "Stats is present in room database")
            val stats = statsDao.getStatsByUID(uid)
            Log.v("Repository", "Stats = ${stats}")
            myCallback.invoke(stats)
        }
        else{
            Log.v("Repository", "Stats is not present in room database... fetchStatsData is called")

            val stats = fetchStatsData{
                result ->
                Log.v("Repository", "Stats updated to ${result}")
                statsDao.insert(result)
                Log.i("FirebaseService", "Stats INSERTED")
                myCallback.invoke(statsDao.getStatsByUID(uid))
            }
            return stats

        }

    }

    suspend fun fetchUserData(myCallback: (result:User)-> Unit){
        Log.v("Repository", "fetchUserData called")

            firebaseService.fetchUser { result ->
                myCallback.invoke(result)
            }

    }

    suspend fun fetchStatsData(myCallback: (result: Stats)-> Unit){
        Log.v("Repository", "fetchStatsData called")

        firebaseService.fetchStats(dao) { result ->
            myCallback.invoke(result)
        }

    }

    suspend fun fetchUpdates(myCallback: (result: User) -> Unit){

        FirebaseService(uid).fetchUpdates{
            localuser = it
            dao.update(it)
            val user = dao.getUserByUID(uid)
            Log.v("Repository","One time update " + it.toString())
            myCallback.invoke(it)

        }

        FirebaseService(uid).fetchContinuosUpdates{
            localuser = it
            dao.update(it)
            val user = dao.getUserByUID(uid)
            Log.v("Repository", "Continuous updates " + it.toString())
            myCallback.invoke(it)

        }

    }


}

