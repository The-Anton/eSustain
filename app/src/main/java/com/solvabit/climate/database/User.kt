package com.solvabit.climate.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solvabit.climate.dataModel.Post
import org.jetbrains.annotations.NotNull

@Entity
data class User(
        @ColumnInfo(name = "active")
        var active: Boolean?,
        var actualForest: Int?,
        var aqi: Double?,
        var aqistatus: Boolean?,
        var city: String?,
        var co: Double?,
        @ColumnInfo(name = "completed_action")
        var completedAction: List<String>,
        var country: String?,
        @ColumnInfo(name = "email")
        var email: String?,
        var forestDensity: Double?,
        var groundWaterData: List<String>,
        @ColumnInfo(name = "image_url")
        var imageUrl: String?,
        @ColumnInfo(name = "location")
        var location: List<String>,
        @ColumnInfo(name = "location_updated")
        var locationUpdated: Boolean?,
        var no2: Double?,
        var noForest: Int?,
        @ColumnInfo(name = "normalized_score")
        var normalizedScore: Double?,
        var o3: Double?,
        @ColumnInfo(name = "remaining_action")
        var remainingAction: List<String>,
        var openForest: Int?,
        var pm10: Double?,
        var pm25: Double?,
        @ColumnInfo(name = "present_action")
        var presentAction: List<String>,
        @ColumnInfo(name = "rating")
        var rating: String?,
        @ColumnInfo(name = "recommended_Target")
        var recommendedTarget: Int?,
        var so2: Double?,
        var state: String?,
        @ColumnInfo(name = "status")
        var status: String?,
        @ColumnInfo(name = "task")
        var task: List<String>,
        @ColumnInfo(name = "time_stamp")
        var timeStamp: Long?,
        var totalArea: Int?,
        @ColumnInfo(name = "trees_planted")
        var treesPlanted: Int?,
        @ColumnInfo(name = "trees_referred")
        var treesReferred: Int?,
        @PrimaryKey @NotNull
        var uid: String,
        @ColumnInfo(name = "updated")
        var updated: Boolean?,
        @ColumnInfo(name = "username")
        var username: String?,
        @ColumnInfo(name = "interestedGroups")
        val interestedGroups: List<Post>
) {


    constructor() : this(null,
            null,
            null,
            false,
            "City",
            null,
            listOf<String>("0", "0"),
            "Country",
            null,
            null,
            listOf<String>("0"),
            null,
            listOf<String>("0.0", "0.0"),
            null,
            null,
            null,
            null,
            null,
            listOf<String>("0", "0"),
            null,
            null,
            null,
            listOf<String>("0", "0"),
            null,
            null,
            null,
            "State",
            "Not Set",
            listOf<String>("0", "0"),
            null,
            null,
            null,
            null,
            "1",
            null,
            null,
            listOf<Post>()
    )


}


class ListTypeConverter() {

    @TypeConverter
    fun fromInteger(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }


    @TypeConverter
    fun fromIntList(array: List<String>): String? {
        return Gson().toJson(array)
    }
}


class PostTypeConverter() {

    @TypeConverter
    fun postListToJson(value: List<Post>?) = Gson().toJson(value)

    @TypeConverter
    fun postJsonToList(value: String) = Gson().fromJson(value, Array<Post>::class.java).toList()

}