package com.example.trendborsa.ui.screen.borsa

import com.example.trendborsa.domain.model.Product

data class PurchaseFeedItem(
    val buyerName: String,
    val price: Double,
    val id: Long = System.currentTimeMillis()
)

data class BorsaUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Event state
    val isEventActive: Boolean = true,
    val remainingSeconds: Int = 0,
    val viewerCount: Int = 0,
    val startPrice: Double = 0.0,
    val lowestPrice: Double = 0.0,
    val highestPrice: Double = 0.0,
    // Stock (from backend)
    val initialStock: Int = 0,
    val remainingStock: Int = 0,
    // Purchase success (P0)
    val showPurchaseSuccess: Boolean = false,
    val purchasePrice: Double = 0.0,
    // Live purchase feed (P1)
    val latestPurchase: PurchaseFeedItem? = null,
    // Purchase in progress flag
    val isPurchasing: Boolean = false
)
