package com.solvabit.climate.dataModel

class PlantedTrees(
    var lat: Double,
    val lon: Double,
    val plant_image: String,
    val time: String,
    val uid: String,
    val username: String,
    val user_image: String
) {
    constructor():this(
            0.0,
            0.0,
            "",
            "",
            "",
            "",
            ""
    )
}