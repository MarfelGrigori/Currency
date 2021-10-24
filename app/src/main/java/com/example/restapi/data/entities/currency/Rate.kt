package com.example.restapi.data.entities.currency

data class Rate(
    val curAbbreviation: String,
    val curID: Int,
    val curName: String,
    val curOfficialRate: Double,
    val curScale: Int,
    val date: String,
    var isFavourite: Boolean = false,
    var prefIndex: Int =0
)
