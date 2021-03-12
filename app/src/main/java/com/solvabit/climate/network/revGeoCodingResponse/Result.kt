package com.example.forests.data.revGeoCodingResponse


import com.google.gson.annotations.SerializedName

data class Result(
    val area: String,
    val city: String,
    val district: String,
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val houseName: String,
    val houseNumber: String,
    val lat: String,
    val lng: String,
    val locality: String,
    val pincode: String,
    val poi: String,
    @SerializedName("poi_dist")
    val poiDist: String,
    val state: String,
    val street: String,
    @SerializedName("street_dist")
    val streetDist: String,
    val subDistrict: String,
    val subLocality: String,
    val subSubLocality: String,
    val village: String
)