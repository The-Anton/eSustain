package com.solvabit.climate.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.NotNull

@Entity
data class User(
        @ColumnInfo(name = "active")
        var active: Boolean?,
        @ColumnInfo(name = "completed_action")
        var completedAction: List<String>,
        @ColumnInfo(name = "email")
        var email: String?,
        @ColumnInfo(name = "image_url")
        var imageUrl: String?,
        @ColumnInfo(name = "location")
        var location:List<String>,
        @ColumnInfo(name = "location_updated")
        var locationUpdated:Boolean?,
        @ColumnInfo(name = "normalized_score")
        var normalizedScore: Int?,
        @ColumnInfo(name = "ongoing_action")
        var ongoingAction: List<String>,
        @ColumnInfo(name = "present_action")
        var presentAction:List<String>,
        @ColumnInfo(name = "rating")
        var rating:String?,
        @ColumnInfo(name = "status")
        var status: String?,
        @ColumnInfo(name = "target_trees")
        var targetTrees:Int?,
        @ColumnInfo(name = "task")
        var task: List<String>,
        @ColumnInfo(name = "time_stamp")
        var timeStamp: Long?,
        @ColumnInfo(name = "trees_referred")
        var treesReferred:Int?,
        @ColumnInfo(name = "trees_planted")
        var treesPlanted:Int?,
        @PrimaryKey @NotNull
        var uid: String,
        @ColumnInfo(name = "updated")
        var updated: Boolean?,
        @ColumnInfo(name = "username")
        var username: String?

        ){


        constructor(): this(null,
                listOf<String>("0","0"),
                null,
                null,
                listOf<String>("0.0","0.0"),
                null,
                null,
                listOf<String>("0","0"),
                listOf<String>("0","0"),
                null,
                null,
                null,
                listOf<String>("0","0"),
                null,
                null,
                null,
                "1",
                null,
                null
        )




}



class ListTypeConverter(){


        @TypeConverter
        fun fromInteger(value:String): List<String>{
                val listType = object: TypeToken<List<String>>(){}.type
                return Gson().fromJson(value,listType)
        }


        @TypeConverter
        fun fromIntList(array:List<String>): String? {
                return Gson().toJson(array)
        }
//
//        @TypeConverter
//        fun fromInteger(value:Int): List<Int>{
//                val listType = object: TypeToken<List<Int>>(){}.type
//                val newValue = value.toString()
//                return Gson().fromJson(value,listType)
//        }
//
//
//        @TypeConverter
//        fun fromIntList(array:List<Int>): String? {
//                return Gson().toJson(array)
//        }
//
//
//
//        @TypeConverter
//        fun fromDouble(value:Double): List<Double>{
//
//                val listType = object: TypeToken<List<Double>>(){}.type
//                var newValue = value.toString()
//                return Gson().fromJson(newValue,listType)
//        }
//
//
//        @TypeConverter
//        fun fromDoubleList(array:List<Double>): String? {
//                return Gson().toJson(array)
//        }
}