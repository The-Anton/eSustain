package com.solvabit.climate.dataModel

data class Post(var post_text:String, val post_image:String,val uid:String,val time:String,val category: String) {
    constructor():this("","","","","")
}