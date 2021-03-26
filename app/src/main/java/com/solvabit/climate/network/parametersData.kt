package com.solvabit.climate.network


import com.google.gson.annotations.SerializedName

data class parametersData(
    val actualForest: Int,
    val aqi: Double,
    val city: String,
    val co: Double,
    val country: String,
    val forestDensity: Double,
    val no2: Double,
    val noForest: Int,
    val normalizedScore: Double,
    val o3: Double,
    val openForest: Int,
    val pm10: Double,
    val pm25: Double,
    val recommendedTarget: Int,
    val so2: Double,
    val state: String,
    val apistatus:Boolean,
    val status: Boolean,
    val totalArea: Int,
    val updated: Boolean,
    val groundWaterData: List<String>
)