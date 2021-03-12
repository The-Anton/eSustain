package com.solvabit.climate.database

import android.content.Context
import androidx.room.*

@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(ListTypeConverter::class)
abstract class UserDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java,
                            "User"
                    ).allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}