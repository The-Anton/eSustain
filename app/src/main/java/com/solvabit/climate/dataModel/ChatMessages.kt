package com.solvabit.climate.dataModel

class ChatMessages(val id: String,
                   val text: String,
                   val fromId: String,
                   val timestamp: Long,
                   val imageUrl: String
) {
    constructor() : this(
            "",
            "",
            "",
            -1,
            ""
    )
}