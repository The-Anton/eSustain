package com.solvabit.climate.dataModel
data class ForestData( var actualforestcover: Long, var geoarea:Long,var  noforest:Long, var openforest:Long,var states:String){
    constructor(): this(0,0,0,0,"")
}