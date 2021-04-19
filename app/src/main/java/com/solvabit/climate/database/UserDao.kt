package com.solvabit.climate.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT COUNT(*) FROM User WHERE uid = :UID")
    fun hasUser(UID: String): Int

    @Query("SELECT * FROM User WHERE uid = :UID")
    fun getUserByUID(UID: String): User

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(uid: User)
}