package com.example.trendborsa.domain.model

data class Product(
    val id: Long,
    val name: String,
    val brandName: String,
    val images: List<String>,
    val favoriteCount: String,
    val categoryRanking: String?,
    val categoryName: String?,
    val categoryHierarchy: String,
    val marketPrice: Double,
    val salePrice: Double,
    val currentPrice: Double,
    val discountPercentage: String,
    val merchantName: String,
    val merchantScore: Double,
    val stockQuantity: Int,
    val ratingText: String,
    val priceChangePercent: Double,
    val isPriceUp: Boolean,
    val priceHistory: List<Double>
)
