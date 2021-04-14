package com.solvabit.climate.Repository

import com.solvabit.climate.database.Stats
import com.solvabit.climate.database.StatsDao
import com.solvabit.climate.database.User
import com.solvabit.climate.database.UserDao
import com.solvabit.climate.fragment.Dashboard.Companion.localuser
import com.solvabit.climate.network.FirebaseService
import timber.log.Timber

class Repository constructor(val dao: UserDao, val uid: String) {

    private val firebaseService = FirebaseService(uid)

     fun getUser(myCallback: (result: User) -> Unit) {
        val userCount = dao.hasUser(uid)
        Timber.tag("Repository").v("User count : ${userCount}")


        if (userCount == 1) {
            Timber.tag("Repository").v("User is present in room database")
            val user = dao.getUserByUID(uid)
            Timber.tag("Repository").v("User updated to ${user}")
            myCallback.invoke(user)
        } else {
            Timber.tag("Repository")
                .v("User is not present in room database... fetchUserData is called")

            val user = fetchUserData { result ->
                Timber.tag("Repository").v("User updated to ${result}")
                dao.insert(result)
                Timber.tag("FirebaseService").v("User INSERTED")
                myCallback.invoke(dao.getUserByUID(uid))
            }
            return user

        }

    }

     fun getStats(statsDao: StatsDao, myCallback: (result: Stats) -> Unit) {
        val hasStats = statsDao.hasStats(uid)
        Timber.tag("Repository").v("Stats count : ${hasStats}")

        if (hasStats == 1) {
            Timber.tag("Repository").v("Stats is present in room database")
            val stats = statsDao.getStatsByUID(uid)
            Timber.tag("Repository").v("Stats = ${stats}")
            myCallback.invoke(stats)
        } else {
            Timber.tag("Repository")
                .v("Stats is not present in room database... fetchStatsData is called")

            val stats = fetchStatsData { result ->
                Timber.tag("Repository").v("Stats updated to ${result}")
                statsDao.insert(result)
                Timber.tag("FirebaseService").v("Stats INSERTED")
                myCallback.invoke(statsDao.getStatsByUID(uid))
            }
            return stats

        }

    }

     fun fetchUserData(myCallback: (result: User) -> Unit) {
        Timber.tag("Repository").v("fetchUserData called")

        firebaseService.fetchUser { result ->
            myCallback.invoke(result)
        }

    }

    fun fetchStatsData(myCallback: (result: Stats) -> Unit) {
        Timber.tag("Repository").v("fetchStatsData called")

        firebaseService.fetchStats(dao) { result ->
            myCallback.invoke(result)
        }

    }

    fun fetchUpdates(myCallback: (result: User) -> Unit) {

        FirebaseService(uid).fetchUpdates {
            localuser = it
            dao.update(it)
            Timber.tag("Repository").v("One time update %s", it.toString())
            myCallback.invoke(it)

        }

        FirebaseService(uid).fetchContinuosUpdates {
            localuser = it
            dao.update(it)
            Timber.tag("Repository").v("Continuous updates %s", it.toString())
            myCallback.invoke(it)

        }

    }


}


