package com.solvabit.climate.utility

import com.solvabit.climate.database.Stats

class StatsUtility() {

    fun parseForestComparision(uid:String, data: MutableMap<String, Any>):Stats{
        val stats = Stats()
        stats.geographical_area = data?.get("geographical_area") as Long
        stats.actual_forest_cover_2009 = data?.get("actual_forest_cover_2009") as Long
        stats.actual_forest_cover_2011 = data?.get("actual_forest_cover_2011") as Long
        stats.actual_forest_cover_2013 = data?.get("actual_forest_cover_2013") as Long
        stats.actual_forest_cover_2015 = data?.get("actual_forest_cover_2015") as Long
        stats.actual_forest_cover_2019 = data?.get("actual_forest_cover_2019") as Long
        stats.very_dense_forest_2009 = data?.get("very_dense_forest_2009") as Long
        stats.very_dense_forest_2011 = data?.get("very_dense_forest_2011") as Long
        stats.very_dense_forest_2013 = data?.get("very_dense_forest_2013") as Long
        stats.very_dense_forest_2015 = data?.get("very_dense_forest_2015") as Long
        stats.very_dense_forest_2019 = data?.get("very_dense_forest_2019") as Long
        stats.moderately_dense_forest_2009 = data?.get("moderately_dense_forest_2009") as Long
        stats.moderately_dense_forest_2011 = data?.get("moderately_dense_forest_2011") as Long
        stats.moderately_dense_forest_2013 = data?.get("moderately_dense_forest_2013") as Long
        stats.moderately_dense_forest_2015 = data?.get("moderately_dense_forest_2015") as Long
        stats.moderately_dense_forest_2019 = data?.get("moderately_dense_forest_2019") as Long
        stats.open_forest_2009 = data?.get("open_forest_2009") as Long
        stats.open_forest_2011 = data?.get("open_forest_2011") as Long
        stats.open_forest_2013 = data?.get("open_forest_2013") as Long
        stats.open_forest_2015 = data?.get("open_forest_2015") as Long
        stats.open_forest_2019 = data?.get("open_forest_2019") as Long
        stats.percentage_of_geographical_area_2009 = data?.get("percentage_of_geographical_area_2009") as Double
        stats.percentage_of_geographical_area_2011 = data?.get("percentage_of_geographical_area_2011") as Double
        stats.percentage_of_geographical_area_2013 = data?.get("percentage_of_geographical_area_2013") as Double
        stats.percentage_of_geographical_area_2015 = data?.get("percentage_of_geographical_area_2015") as Double
        stats.percentage_of_geographical_area_2019 = data?.get("percentage_of_geographical_area_2019") as Double
        stats.uid = uid

        return stats
    }

}