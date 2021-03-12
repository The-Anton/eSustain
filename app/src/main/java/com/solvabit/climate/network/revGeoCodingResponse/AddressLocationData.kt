package com.example.forests.data.revGeoCodingResponse


import com.google.gson.annotations.SerializedName

data class AddressLocationData(
    val responseCode: Int,
    val results: List<Result>,
    val version: String
)