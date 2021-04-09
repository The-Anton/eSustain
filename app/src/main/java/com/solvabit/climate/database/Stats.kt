package com.solvabit.climate.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solvabit.climate.dataModel.Post
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Type

@Entity
data class Stats(
                  @PrimaryKey @NotNull
                  var uid: String,
                  @ColumnInfo(name = "interestedGroups")
                  val interestedGroups: Map<String, Post>
) {


    constructor() : this(
        " ",
        mapOf()
    )

}


class ForestCoverComparisionTypeConverter() {

    @TypeConverter
    fun fromString(value: String?): Map<String?, Post?>? {
        val mapType: Type = object : TypeToken<Map<String?, Post?>?>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringMap(map: Map<String?, Post?>?): String? {
        val gson = Gson()
        return gson.toJson(map)
    }
}