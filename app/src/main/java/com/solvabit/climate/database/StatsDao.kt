package com.solvabit.climate.database

import androidx.room.*

@Dao
interface StatsDao {
    @Query("SELECT COUNT(*) FROM Stats WHERE uid = :UID")
    fun hasStats(UID: String): Int

    @Query("SELECT * FROM User WHERE uid = :UID")
    fun getStatsByUID(UID: String): User

    @Insert
    fun insert(stats : Stats)

    @Update
    fun update(stats : Stats)

    @Delete
    fun delete(uid: User)
}