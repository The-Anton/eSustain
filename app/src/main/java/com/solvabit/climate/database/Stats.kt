package com.solvabit.climate.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity

data class Stats(
    var geographical_area: Long,
    var actual_forest_cover_2009: Double,
    var actual_forest_cover_2011: Double,
    var actual_forest_cover_2013: Double,
    var actual_forest_cover_2015: Double,
    var actual_forest_cover_2019: Double,
    var very_dense_forest_2009: Double,
    var very_dense_forest_2011: Double,
    var very_dense_forest_2013: Double,
    var very_dense_forest_2015: Double,
    var very_dense_forest_2019: Double,
    var moderately_dense_forest_2009: Double,
    var moderately_dense_forest_2011: Double,
    var moderately_dense_forest_2013: Double,
    var moderately_dense_forest_2015: Double,
    var moderately_dense_forest_2019: Double,
    var open_forest_2009: Double,
    var open_forest_2011: Double,
    var open_forest_2013: Double,
    var open_forest_2015: Double,
    var open_forest_2019: Double,
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
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        0.00,
        " "
    )

}
