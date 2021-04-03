package com.solvabit.climate.dataModel

class PlantedTrees(
    var lat: Double,
    var lon: Double,
    var plant_image: String,
    var time: String,
    var uid: String,
    var username: String,
    var user_image: String
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