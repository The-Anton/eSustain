package com.solvabit.climate.database

data class SingleAction(
        val id: Int,
        val title: String,
        val sub_title: String,
        val category: String,
        val number: Int,
        val background: Int,
        val achievement_logo: String) {
    constructor() : this(
            0,
            "",
            "",
            "",
            0,
            0,
            ""
    )
}