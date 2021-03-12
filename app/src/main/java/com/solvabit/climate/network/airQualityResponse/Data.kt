package com.example.forests.data.airQualityResponse


import com.google.gson.annotations.SerializedName

data class Data(
    val aqi: Double,
    val co: Double,
    @SerializedName("mold_level")
    val moldLevel: Double,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm25: Double,
    @SerializedName("pollen_level_grass")
    val pollenLevelGrass: Int,
    @SerializedName("pollen_level_tree")
    val pollenLevelTree: Int,
    @SerializedName("pollen_level_weed")
    val pollenLevelWeed: Int,
    @SerializedName("predominant_pollen_type")
    val predominantPollenType: String,
    val so2: Double
)