package com.example.trendborsa.domain.repository

import com.example.trendborsa.data.remote.dto.ConfirmResponse
import com.example.trendborsa.data.remote.dto.LockResponse
import com.example.trendborsa.domain.model.Product

data class ProductWithDropInfo(
    val product: Product,
    val totalStock: Int,
    val remainingStock: Int,
    val remainingSeconds: Int
)

interface ProductRepository {
    suspend fun getProduct(): Result<ProductWithDropInfo>
    suspend fun lockPrice(dropId: String, userId: String, quantity: Int): Result<LockResponse>
    suspend fun confirmPurchase(dropId: String, lockId: String): Result<ConfirmResponse>
}
