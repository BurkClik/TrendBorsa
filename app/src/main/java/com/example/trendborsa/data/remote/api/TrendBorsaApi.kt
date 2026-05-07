package com.example.trendborsa.data.remote.api

import com.example.trendborsa.data.remote.dto.ProductDto
import retrofit2.http.GET

interface TrendBorsaApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
