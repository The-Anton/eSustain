package com.solvabit.climate.dataModel

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull


data class Userdata(
                    var active: Boolean?,
                    var completedAction: Int?,
                    var email: String?,
                    var imageUrl: String?,
                    var lattitude:Double?,
                    var longitude:Double?,
                    var normalizedScore: Int?,
                    var ongoingAction: Int?,
                    var presentAction:Int?,
                    var rating:String?,
                    var status: String?,
                    var targetTrees:Int?,
                    var task: Int?,
                    var timeStamp: Long?,
                    var treesReferred:Int?,
                    var treesPlanted:Int?,
                    var uid: String,
                    var updated: Boolean?,
                    var username: String?


){
    constructor(): this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        "null",
        null,
        null,


    )
}
