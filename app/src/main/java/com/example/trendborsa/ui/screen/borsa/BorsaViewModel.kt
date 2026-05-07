package com.example.trendborsa.ui.screen.borsa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendborsa.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BorsaViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BorsaUiState())
    val uiState: StateFlow<BorsaUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getProduct()
                .onSuccess { product ->
                    _uiState.update {
                        it.copy(
                            product = product,
                            isLoading = false,
                            startPrice = product.salePrice,
                            lowestPrice = product.salePrice,
                            highestPrice = product.salePrice,
                            viewerCount = Random.nextInt(800, 1501)
                        )
                    }
                    startPriceTicker()
                    startCountdown()
                    startViewerSimulation()
                    startStockSimulation()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)
                _uiState.update { state ->
                    val newRemaining = (state.remainingSeconds - 1).coerceAtLeast(0)
                    state.copy(
                        remainingSeconds = newRemaining,
                        isEventActive = newRemaining > 0
                    )
                }
            }
        }
    }

    private fun startViewerSimulation() {
        viewModelScope.launch {
            while (_uiState.value.isEventActive) {
                delay(Random.nextLong(3000, 5001))
                if (!_uiState.value.isEventActive) break
                _uiState.update { state ->
                    val delta = Random.nextInt(-50, 51)
                    state.copy(viewerCount = (state.viewerCount + delta).coerceAtLeast(100))
                }
            }
        }
    }

    private fun startStockSimulation() {
        viewModelScope.launch {
            while (_uiState.value.isEventActive) {
                delay(Random.nextLong(1500, 2501))
                if (!_uiState.value.isEventActive) break
                _uiState.update { state ->
                    val decrease = Random.nextInt(5, 13)
                    state.copy(
                        remainingStock = (state.remainingStock - decrease).coerceAtLeast(0)
                    )
                }
            }
        }
    }

    private fun startPriceTicker() {
        viewModelScope.launch {
            while (true) {
                delay(Random.nextLong(2000, 3500))
                if (!_uiState.value.isEventActive) break
                updatePrice()
            }
        }
    }

    private fun updatePrice() {
        _uiState.update { state ->
            val product = state.product ?: return@update state
            val changePercent = Random.nextDouble(-5.0, 5.0)
            val newPrice = product.currentPrice * (1 + changePercent / 100.0)
            val clampedPrice = newPrice.coerceIn(
                product.salePrice * 0.5,
                product.salePrice * 1.5
            )
            val history = (product.priceHistory + clampedPrice).takeLast(30)
            val totalChange = ((clampedPrice - product.salePrice) / product.salePrice) * 100.0

            state.copy(
                product = product.copy(
                    currentPrice = clampedPrice,
                    priceChangePercent = totalChange,
                    isPriceUp = changePercent >= 0,
                    priceHistory = history
                ),
                lowestPrice = minOf(state.lowestPrice, clampedPrice),
                highestPrice = maxOf(state.highestPrice, clampedPrice)
            )
        }
    }
}
