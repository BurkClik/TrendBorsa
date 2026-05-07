package com.example.trendborsa.data.remote.api

import com.example.trendborsa.data.remote.dto.ConfirmRequest
import com.example.trendborsa.data.remote.dto.ConfirmResponse
import com.example.trendborsa.data.remote.dto.DropDto
import com.example.trendborsa.data.remote.dto.LockRequest
import com.example.trendborsa.data.remote.dto.LockResponse
import com.example.trendborsa.data.remote.dto.ProductDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrendBorsaApi {

    @GET("drops")
    suspend fun getDrops(): List<ProductDto>

    @GET("drops/{id}")
    suspend fun getDrop(@Path("id") id: String): DropDto

    @POST("drops/{id}/lock")
    suspend fun lockPrice(@Path("id") id: String, @Body request: LockRequest): LockResponse

    @POST("drops/{id}/confirm")
    suspend fun confirmPurchase(@Path("id") id: String, @Body request: ConfirmRequest): ConfirmResponse
}
