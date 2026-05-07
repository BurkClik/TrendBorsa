package com.example.trendborsa.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LockRequest(
    @SerialName("user_id") val userId: String,
    val quantity: Int
)

@Serializable
data class LockResponse(
    val lock: PriceLockDto,
    val message: String
)

@Serializable
data class PriceLockDto(
    val id: String,
    @SerialName("drop_id") val dropId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("locked_price") val lockedPrice: Double,
    val quantity: Int,
    val status: String,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("expires_at") val expiresAt: String = ""
)

@Serializable
data class ConfirmRequest(
    @SerialName("lock_id") val lockId: String
)

@Serializable
data class ConfirmResponse(
    val purchase: PurchaseDto,
    val message: String
)

@Serializable
data class PurchaseDto(
    val id: String,
    @SerialName("drop_id") val dropId: String,
    @SerialName("user_id") val userId: String = "",
    val price: Double,
    val quantity: Int,
    @SerialName("created_at") val createdAt: String = ""
)
