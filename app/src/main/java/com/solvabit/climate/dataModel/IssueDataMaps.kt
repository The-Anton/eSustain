package com.solvabit.climate.dataModel

class IssueDataMaps (
    var lat: Double,
    var lon: Double,
    var time: String,
    var uid: String,
    var category:String,
    var group_name:String,
    var key:String,
    var post_image:String,
    var post_text:String,
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
        "",
        "",
        "",
        "",
        ""
    )
}