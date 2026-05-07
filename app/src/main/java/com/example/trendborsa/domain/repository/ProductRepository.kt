package com.example.trendborsa.domain.repository

import com.example.trendborsa.domain.model.Product

interface ProductRepository {
    suspend fun getProduct(): Result<Product>
}
