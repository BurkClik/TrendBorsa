package com.example.trendborsa.ui.screen.borsa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendborsa.data.remote.ws.TrendBorsaWebSocket
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
    private val repository: ProductRepository,
    private val webSocket: TrendBorsaWebSocket
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
                .onSuccess { dropInfo ->
                    val product = dropInfo.product
                    _uiState.update {
                        it.copy(
                            product = product,
                            isLoading = false,
                            startPrice = product.salePrice,
                            lowestPrice = product.currentPrice,
                            highestPrice = product.currentPrice,
                            viewerCount = Random.nextInt(800, 1501),
                            initialStock = dropInfo.totalStock,
                            remainingStock = dropInfo.remainingStock,
                            remainingSeconds = dropInfo.remainingSeconds
                        )
                    }
                    startPriceWebSocket()
                    startCountdown()
                    startViewerSimulation()
                    startFakeBuyerSimulation()
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to load product", e)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun startPriceWebSocket() {
        Log.d(TAG, "Starting WebSocket for drop=$DROP_ID")
        webSocket.connect(DROP_ID)
        viewModelScope.launch {
            webSocket.events.collect { event ->
                Log.d(TAG, "WS event: type=${event.type} price=${event.price}")
                if (event.type != "PRICE_UPDATE") return@collect
                if (event.dropId != DROP_ID) return@collect

                _uiState.update { state ->
                    val product = state.product ?: return@update state
                    val newPrice = event.price
                    val isPriceUp = event.direction == "UP"
                    val history = (product.priceHistory + newPrice).takeLast(30)
                    val totalChange = ((newPrice - product.salePrice) / product.salePrice) * 100.0

                    state.copy(
                        product = product.copy(
                            currentPrice = newPrice,
                            priceChangePercent = totalChange,
                            isPriceUp = isPriceUp,
                            priceHistory = history
                        ),
                        lowestPrice = minOf(state.lowestPrice, newPrice),
                        highestPrice = maxOf(state.highestPrice, newPrice)
                    )
                }
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

    fun onPurchase() {
        val state = _uiState.value
        if (state.product == null || !state.isEventActive || state.isPurchasing) return

        _uiState.update { it.copy(isPurchasing = true) }

        viewModelScope.launch {
            // Step 1: Lock price
            repository.lockPrice(DROP_ID, USER_ID, 1)
                .onSuccess { lockResponse ->
                    val lockId = lockResponse.lock.id
                    val lockedPrice = lockResponse.lock.lockedPrice
                    Log.d(TAG, "Price locked: $lockedPrice (lockId=$lockId)")

                    // Step 2: Confirm purchase
                    repository.confirmPurchase(DROP_ID, lockId)
                        .onSuccess { confirmResponse ->
                            Log.d(TAG, "Purchase confirmed: ${confirmResponse.purchase.id}")
                            _uiState.update {
                                it.copy(
                                    showPurchaseSuccess = true,
                                    purchasePrice = lockedPrice,
                                    remainingStock = (it.remainingStock - 1).coerceAtLeast(0),
                                    isPurchasing = false
                                )
                            }
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Confirm failed", e)
                            _uiState.update { it.copy(isPurchasing = false) }
                        }
                }
                .onFailure { e ->
                    Log.e(TAG, "Lock failed", e)
                    _uiState.update { it.copy(isPurchasing = false) }
                }
        }
    }

    private val fakeBuyerNames = listOf(
        "Mehmet", "Ayse", "Ali", "Fatma", "Emre", "Zeynep",
        "Elif", "Can", "Selin", "Oguz", "Deniz", "Hakan", "Merve",
        "Serkan", "Gul", "Tolga", "Derya", "Cem", "Sude"
    )

    private fun startFakeBuyerSimulation() {
        viewModelScope.launch {
            delay(5000) // initial wait
            while (_uiState.value.isEventActive) {
                delay(Random.nextLong(5000, 15001))
                if (!_uiState.value.isEventActive) break

                val botId = "bot-${Random.nextInt(10000)}"
                val buyerName = fakeBuyerNames.random()

                repository.lockPrice(DROP_ID, botId, 1)
                    .onSuccess { lockResponse ->
                        repository.confirmPurchase(DROP_ID, lockResponse.lock.id)
                            .onSuccess {
                                Log.d(TAG, "🤖 Fake buy: $buyerName @ ${lockResponse.lock.lockedPrice}")
                                _uiState.update { state ->
                                    state.copy(
                                        latestPurchase = PurchaseFeedItem(
                                            buyerName = buyerName,
                                            price = lockResponse.lock.lockedPrice
                                        ),
                                        remainingStock = (state.remainingStock - 1).coerceAtLeast(0)
                                    )
                                }
                                delay(2500)
                                _uiState.update { it.copy(latestPurchase = null) }
                            }
                    }
                    .onFailure { e ->
                        Log.d(TAG, "🤖 Fake buy failed: ${e.message}")
                    }
            }
        }
    }

    fun dismissPurchaseSuccess() {
        _uiState.update { it.copy(showPurchaseSuccess = false) }
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.disconnect()
    }

    companion object {
        private const val TAG = "BorsaViewModel"
        private const val DROP_ID = "drop-1"
        private const val USER_ID = "android-user-1"
    }
}
