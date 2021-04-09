package com.solvabit.climate.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ForestCoverComparision(
        var geographical_area: Double,
        var actual_forest_cover_2009: Double,
        var actual_forest_cover_2011: Double,
        var actual_forest_cover_2013:Double,
        var actual_forest_cover_2015: Double,
        var actual_forest_cover_2019:Double,
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
        var percentage_of_geographical_area_2019: Double
       ) : Parcelable {

    constructor() : this(
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
            0.00,









    )
}