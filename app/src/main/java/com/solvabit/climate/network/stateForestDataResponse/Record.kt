package com.example.forests.data.stateForestDataResponse


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("_area")
    val area: String,
    @SerializedName("geographical_area")
    val geographicalArea: String,
    @SerializedName("percent_of_ga")
    val percentOfGa: String,
    @SerializedName("states_uts")
    val statesUts: String
)