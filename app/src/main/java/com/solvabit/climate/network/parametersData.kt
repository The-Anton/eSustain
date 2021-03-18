package com.solvabit.climate.network


import com.google.gson.annotations.SerializedName

data class parametersData(
    val aqi: Int,
    val co: Double,
    val no2: Double,
    val normalizedScore: Double,
    val o3: Double,
    val pm10: Double,
    val pm25: Double,
    val so2: Double,
    val targetTrees: Int
)