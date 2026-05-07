package com.example.trendborsa.data.repository

import com.example.trendborsa.data.mock.MockProductDataSource
import com.example.trendborsa.data.remote.api.TrendBorsaApi
import com.example.trendborsa.data.remote.dto.ConfirmRequest
import com.example.trendborsa.data.remote.dto.ConfirmResponse
import com.example.trendborsa.data.remote.dto.LockRequest
import com.example.trendborsa.data.remote.dto.LockResponse
import com.example.trendborsa.domain.repository.ProductRepository
import com.example.trendborsa.domain.repository.ProductWithDropInfo
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: TrendBorsaApi
) : ProductRepository {

    override suspend fun getProduct(): Result<ProductWithDropInfo> {
        return try {
            val drop = api.getDrop(DROP_ID)
            val mock = MockProductDataSource.getProduct()

            val merged = mock.copy(
                salePrice = drop.startPrice,
                currentPrice = drop.currentPrice,
                marketPrice = drop.startPrice,
                stockQuantity = drop.remainingStock,
                priceHistory = listOf(drop.startPrice)
            )

            val remainingSeconds = try {
                // Go marshals time.Time as RFC3339: 2006-01-02T15:04:05.999999999Z or +00:00
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                // Strip fractional seconds and timezone suffix, parse as UTC
                val cleaned = drop.endsAt
                    .replace(Regex("[Zz]$"), "")
                    .replace(Regex("[+-]\\d{2}:\\d{2}$"), "")
                    .replace(Regex("\\.\\d+"), "")
                val endsAt = sdf.parse(cleaned)?.time ?: 0L
                val now = System.currentTimeMillis()
                ((endsAt - now) / 1000).toInt().coerceAtLeast(0)
            } catch (_: Exception) {
                20 * 60 // fallback 20 min
            }

            Result.success(
                ProductWithDropInfo(
                    product = merged,
                    totalStock = drop.totalStock,
                    remainingStock = drop.remainingStock,
                    remainingSeconds = remainingSeconds
                )
            )
        } catch (e: Exception) {
            // Fallback to mock if backend unreachable
            try {
                val mock = MockProductDataSource.getProduct()
                Result.success(
                    ProductWithDropInfo(
                        product = mock,
                        totalStock = 1000,
                        remainingStock = 1000,
                        remainingSeconds = 20 * 60
                    )
                )
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun lockPrice(
        dropId: String,
        userId: String,
        quantity: Int
    ): Result<LockResponse> {
        return try {
            val response = api.lockPrice(dropId, LockRequest(userId, quantity))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmPurchase(
        dropId: String,
        lockId: String
    ): Result<ConfirmResponse> {
        return try {
            val response = api.confirmPurchase(dropId, ConfirmRequest(lockId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val DROP_ID = "drop-1"
    }
}
