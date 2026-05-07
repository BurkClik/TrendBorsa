package com.example.trendborsa.ui.screen.borsa

import com.example.trendborsa.domain.model.Product

data class BorsaUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Event state
    val isEventActive: Boolean = true,
    val remainingSeconds: Int = 2 * 60,
    val viewerCount: Int = 0,
    val startPrice: Double = 0.0,
    val lowestPrice: Double = 0.0,
    val highestPrice: Double = 0.0,
    // Stock
    val initialStock: Int = 1000,
    val remainingStock: Int = 1000
)
