package com.solvabit.climate.database

data class SingleAction(
    val id: Int,
    val topic:String,
    val subtopic: String,
    val value: Int) {
    constructor():this(
        0,
        "",
        "",
        0
    )
}