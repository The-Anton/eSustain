package com.example.forests.data.airQualityResponse


import com.google.gson.annotations.SerializedName

data class AirQualityData(
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("country_code")
    val countryCode: String,
    val `data`: List<Data>,
    val lat: Double,
    val lon: Double,
    @SerializedName("state_code")
    val stateCode: String,
    val timezone: String
)