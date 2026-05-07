package com.example.trendborsa.data.mapper

import com.example.trendborsa.data.remote.dto.ProductDto
import com.example.trendborsa.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        brandName = brandName,
        images = images,
        favoriteCount = favoriteCount,
        categoryRanking = categoryRanking,
        categoryName = categoryName,
        categoryHierarchy = categoryHierarchy,
        marketPrice = marketPrice,
        salePrice = salePrice,
        currentPrice = salePrice,
        discountPercentage = discountPercentage,
        merchantName = merchantName,
        merchantScore = merchantScore,
        stockQuantity = stockQuantity,
        ratingText = ratingText,
        priceChangePercent = 0.0,
        isPriceUp = true,
        priceHistory = listOf(salePrice)
    )
}
