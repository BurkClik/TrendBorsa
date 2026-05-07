package com.example.trendborsa.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val brandName: String = "",
    val images: List<String> = emptyList(),
    val favoriteCount: String = "",
    val categoryRanking: String? = null,
    val categoryName: String? = null,
    val categoryHierarchy: String = "",
    val marketPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val discountPercentage: String = "",
    val merchantName: String = "",
    val merchantScore: Double = 0.0,
    val stockQuantity: Int = 0,
    val ratingText: String = ""
)
