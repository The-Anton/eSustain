package com.solvabit.climate.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(var post_text: String,
                val post_image: String,
                val uid: String,
                val time: String,
                val category: String,
                var likes: List<String>,
                val interested: Int): Parcelable {

    constructor():this(
            "",
            "",
            "",
            "",
            "",
            listOf<String>(),
            0)
}