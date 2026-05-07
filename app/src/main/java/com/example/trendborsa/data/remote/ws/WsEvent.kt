package com.example.trendborsa.data.remote.ws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsEvent(
    val type: String,
    @SerialName("drop_id") val dropId: String = "",
    val price: Double = 0.0,
    @SerialName("prev_price") val prevPrice: Double = 0.0,
    val direction: String = ""
)
