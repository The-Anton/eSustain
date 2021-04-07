package com.solvabit.climate.dataModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(
        var post_text: String,
        val post_image: String,
        val uid: String,
        val time: String,
        val category: String,
        val key: String,
        val group_name: String,
        val interested: Int) : Parcelable {

    constructor() : this(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            0,
    )
}