package com.esei.grvidal.nighttime.network

import com.squareup.moshi.Json

data class BarApi(
    val id: Long,
    @Json(name = "owner") val barOwner: String

)

data class MarsProperty(
    val id: String,
    @Json(name = "img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double
)