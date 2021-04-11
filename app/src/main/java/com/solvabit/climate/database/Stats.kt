package com.solvabit.climate.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.solvabit.climate.dataModel.ForestCoverComparision
import com.solvabit.climate.dataModel.Post
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Type

@Entity

data class Stats(
                    var geographical_area: Long,
                    var actual_forest_cover_2009: Long,
                    var actual_forest_cover_2011: Long,
                    var actual_forest_cover_2013:Long,
                    var actual_forest_cover_2015: Long,
                    var actual_forest_cover_2019:Long,
                    var very_dense_forest_2009: Long,
                    var very_dense_forest_2011: Long,
                    var very_dense_forest_2013: Long,
                    var very_dense_forest_2015: Long,
                    var very_dense_forest_2019: Long,
                    var moderately_dense_forest_2009: Long,
                    var moderately_dense_forest_2011: Long,
                    var moderately_dense_forest_2013: Long,
                    var moderately_dense_forest_2015: Long,
                    var moderately_dense_forest_2019: Long,
                    var open_forest_2009: Long,
                    var open_forest_2011: Long,
                    var open_forest_2013: Long,
                    var open_forest_2015: Long,
                    var open_forest_2019: Long,
                    var percentage_of_geographical_area_2009: Double,
                    var percentage_of_geographical_area_2011: Double,
                    var percentage_of_geographical_area_2013: Double,
                    var percentage_of_geographical_area_2015: Double,
                    var percentage_of_geographical_area_2019: Double,
                  @PrimaryKey @NotNull
                  var uid: String
) {


    constructor() : this(

            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
        " "
    )

}