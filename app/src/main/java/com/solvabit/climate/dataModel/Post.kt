package com.solvabit.climate.dataModel

data class Post(var post_text: String,
                val post_image: String,
                val uid: String,
                val time: String,
                val category: String,
                var likes: MutableList<String>,
                val interested: Int) {

    constructor():this(
            "",
            "",
            "",
            "",
            "",
            mutableListOf<String>("0.0","0.0"),
            0)

}