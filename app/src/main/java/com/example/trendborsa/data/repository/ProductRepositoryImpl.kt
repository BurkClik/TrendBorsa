package com.example.trendborsa.data.repository

import com.example.trendborsa.data.mock.MockProductDataSource
import com.example.trendborsa.domain.model.Product
import com.example.trendborsa.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor() : ProductRepository {

    override suspend fun getProduct(): Result<Product> {
        return try {
            Result.success(MockProductDataSource.getProduct())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
