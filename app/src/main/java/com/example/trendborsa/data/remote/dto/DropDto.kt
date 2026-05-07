package com.example.trendborsa.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropDto(
    val id: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_image") val productImage: String = "",
    val description: String = "",
    @SerialName("start_price") val startPrice: Double,
    @SerialName("current_price") val currentPrice: Double,
    @SerialName("floor_price") val floorPrice: Double = 0.0,
    @SerialName("ceiling_price") val ceilingPrice: Double = 0.0,
    @SerialName("total_stock") val totalStock: Int,
    @SerialName("remaining_stock") val remainingStock: Int,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    val status: String,
    val volatility: Double = 0.0,
    @SerialName("created_at") val createdAt: String = ""
)
