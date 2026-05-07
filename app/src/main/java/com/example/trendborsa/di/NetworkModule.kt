package com.example.trendborsa.di

import com.example.trendborsa.data.remote.api.TrendBorsaApi
import com.example.trendborsa.data.remote.ws.TrendBorsaWebSocket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_HOST = "10.0.2.2:8080"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://$BASE_HOST/api/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideTrendBorsaApi(retrofit: Retrofit): TrendBorsaApi {
        return retrofit.create(TrendBorsaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrendBorsaWebSocket(okHttpClient: OkHttpClient, json: Json): TrendBorsaWebSocket {
        return TrendBorsaWebSocket(okHttpClient, json, "ws://$BASE_HOST")
    }
}
