package com.example.forests.data.stateForestDataResponse


import com.google.gson.annotations.SerializedName

data class StateForestData(
    val active: String,
    @SerializedName("catalog_uuid")
    val catalogUuid: String,
    val count: Int,
    val created: Int,
    @SerializedName("created_date")
    val createdDate: String,
    @SerializedName("external_ws")
    val externalWs: String,
    @SerializedName("external_ws_url")
    val externalWsUrl: String,
    val `field`: List<Field>,
    @SerializedName("index_name")
    val indexName: String,
    val limit: String,
    val message: String,
    val offset: String,
    val org: List<String>,
    @SerializedName("org_type")
    val orgType: String,
    val records: List<Record>,
    val sector: List<String>,
    val source: String,
    val status: String,
    val title: String,
    val total: Int,
    val updated: Int,
    @SerializedName("updated_date")
    val updatedDate: String,
    val version: String,
    val visualizable: String
)