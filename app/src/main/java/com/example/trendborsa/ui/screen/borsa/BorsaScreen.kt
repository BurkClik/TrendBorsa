package com.example.trendborsa.ui.screen.borsa

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.trendborsa.domain.model.Product
import com.example.trendborsa.ui.theme.AuctionHeaderEnd
import com.example.trendborsa.ui.theme.AuctionHeaderStart
import com.example.trendborsa.ui.theme.BorsaDarkGreen
import com.example.trendborsa.ui.theme.BorsaGreen
import com.example.trendborsa.ui.theme.BorsaRed
import com.example.trendborsa.ui.theme.DiscountBadgeRed
import com.example.trendborsa.ui.theme.LiveRedBadge
import com.example.trendborsa.ui.theme.MediumGray
import com.example.trendborsa.ui.theme.StarYellow
import com.example.trendborsa.ui.theme.StockWarningOrange
import com.example.trendborsa.ui.theme.TrendyolOrange

@Composable
fun BorsaScreen(
    viewModel: BorsaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AuctionHeaderBar(
                isEventActive = uiState.isEventActive,
                remainingSeconds = uiState.remainingSeconds,
                viewerCount = uiState.viewerCount
            )
        },
        bottomBar = {
            uiState.product?.let { product ->
                SepeteEkleBar(product = product)
            }
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TrendyolOrange
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                uiState.product != null -> {
                    ProductDetailContent(
                        product = uiState.product!!,
                        startPrice = uiState.startPrice,
                        lowestPrice = uiState.lowestPrice,
                        highestPrice = uiState.highestPrice,
                        initialStock = uiState.initialStock,
                        remainingStock = uiState.remainingStock
                    )
                }
            }
        }
    }
}

// ─── Auction Header Bar ──────────────────────────────────────────────

@Composable
private fun AuctionHeaderBar(
    isEventActive: Boolean,
    remainingSeconds: Int,
    viewerCount: Int
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(AuctionHeaderStart, AuctionHeaderEnd)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        IconButton(
            onClick = { },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // CANLI / SONA ERDİ badge
        if (isEventActive) {
            Row(
                modifier = Modifier
                    .background(LiveRedBadge.copy(alpha = pulseAlpha * 0.9f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CANLI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "SONA ERDİ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(MediumGray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Viewer count
        Text(
            text = "\uD83D\uDC65 %,d kişi izliyor".format(viewerCount),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1
        )

        Spacer(modifier = Modifier.weight(1f))

        // Timer
        Text(
            text = "⏱ $timerText",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (remainingSeconds <= 30) BorsaRed else Color.White
        )
    }
}

// ─── Stats Row ───────────────────────────────────────────────────────

@Composable
private fun StatsRow(
    startPrice: Double,
    lowestPrice: Double,
    highestPrice: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "Başlangıç",
            value = "%.0f TL".format(startPrice),
            valueColor = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "En Düşük",
            value = "%.0f TL ↓".format(lowestPrice),
            valueColor = BorsaGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "En Yüksek",
            value = "%.0f TL ↑".format(highestPrice),
            valueColor = BorsaRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MediumGray,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            maxLines = 1
        )
    }
}

// ─── Product Detail Content ──────────────────────────────────────────

@Composable
private fun ProductDetailContent(
    product: Product,
    startPrice: Double,
    lowestPrice: Double,
    highestPrice: Double,
    initialStock: Int,
    remainingStock: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Image Carousel
        ImageCarousel(images = product.images)

        // 2. Bestseller Badge
        if (product.categoryRanking != null) {
            BestsellerBadge(
                ranking = product.categoryRanking,
                categoryName = product.categoryName
            )
        }

        // 3. Brand + Name
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = product.brandName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                fontSize = 14.sp,
                color = MediumGray
            )
        }

        // 4. Rating + Favorites
        RatingAndFavorites(
            ratingText = product.ratingText,
            favoriteCount = product.favoriteCount
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color(0xFFEEEEEE)
        )

        // 5. Price Section
        PriceSection(product = product)

        // 5.5 Stock Indicator
        StockIndicator(
            remainingStock = remainingStock,
            initialStock = initialStock
        )

        // 6. Stats Row (Başlangıç / En Düşük / En Yüksek)
        StatsRow(
            startPrice = startPrice,
            lowestPrice = lowestPrice,
            highestPrice = highestPrice
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color(0xFFEEEEEE)
        )

        // 7. Mini Price Chart
        if (product.priceHistory.size > 1) {
            MiniPriceChart(
                priceHistory = product.priceHistory,
                isPriceUp = product.isPriceUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFEEEEEE)
            )
        }

        // 8. Merchant Info
        MerchantInfo(
            merchantName = product.merchantName,
            score = product.merchantScore
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Page indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) TrendyolOrange
                            else Color.LightGray
                        )
                )
            }
        }

        // Image counter
        Text(
            text = "${pagerState.currentPage + 1}/${images.size}",
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun BestsellerBadge(ranking: String, categoryName: String?) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\uD83C\uDFC6",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = ranking,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TrendyolOrange
            )
            if (categoryName != null) {
                Text(
                    text = categoryName,
                    fontSize = 11.sp,
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
private fun RatingAndFavorites(ratingText: String, favoriteCount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stars
        repeat(5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = StarYellow
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = ratingText,
            fontSize = 12.sp,
            color = MediumGray
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = BorsaRed
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$favoriteCount kişi favoriledi",
            fontSize = 12.sp,
            color = MediumGray
        )
    }
}

@Composable
private fun PriceSection(product: Product) {
    val priceColor by animateColorAsState(
        targetValue = if (product.isPriceUp) BorsaGreen else BorsaRed,
        animationSpec = tween(300),
        label = "priceColor"
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Original price (strikethrough) + discount badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%.2f TL".format(product.marketPrice),
                fontSize = 14.sp,
                color = MediumGray,
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = product.discountPercentage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(DiscountBadgeRed, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Live price (borsa)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%.2f TL".format(product.currentPrice),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = priceColor
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Change badge
            val changePrefix = if (product.priceChangePercent >= 0) "+" else ""
            val changeArrow = if (product.isPriceUp) "▲" else "▼"
            Text(
                text = "$changeArrow $changePrefix%.1f%%".format(product.priceChangePercent),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = priceColor,
                modifier = Modifier
                    .background(
                        priceColor.copy(alpha = 0.1f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "10 Gunun En Dusuk Fiyati" label
        Text(
            text = "\uD83D\uDCC9 10 Günün En Düşük Fiyatı",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = BorsaDarkGreen,
            modifier = Modifier
                .background(BorsaDarkGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ─── Stock Indicator ────────────────────────────────────────────────

@Composable
private fun StockIndicator(
    remainingStock: Int,
    initialStock: Int
) {
    val ratio = if (initialStock > 0) remainingStock.toFloat() / initialStock else 0f
    val percent = (ratio * 100).toInt()

    val barColor = when {
        ratio > 0.5f -> BorsaGreen
        ratio > 0.2f -> StockWarningOrange
        else -> BorsaRed
    }

    val isUrgent = ratio <= 0.2f

    val infiniteTransition = rememberInfiniteTransition(label = "stockPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUrgent) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stockPulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "\uD83D\uDD25 Son $remainingStock ürün kaldı!",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = barColor.copy(alpha = if (isUrgent) pulseAlpha else 1f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor.copy(alpha = if (isUrgent) pulseAlpha else 1f))
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%$percent kaldı",
                fontSize = 11.sp,
                color = MediumGray
            )
            if (isUrgent) {
                Text(
                    text = "Tükenmek üzere!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BorsaRed
                )
            }
        }
    }
}

@Composable
private fun MiniPriceChart(
    priceHistory: List<Double>,
    isPriceUp: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isPriceUp) BorsaGreen else BorsaRed

    Column(modifier = modifier) {
        Text(
            text = "Fiyat Grafiği",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (priceHistory.size < 2) return@Canvas

            val minPrice = priceHistory.min()
            val maxPrice = priceHistory.max()
            val priceRange = (maxPrice - minPrice).coerceAtLeast(0.01)

            val stepX = size.width / (priceHistory.size - 1)

            val path = Path()
            priceHistory.forEachIndexed { index, price ->
                val x = index * stepX
                val y = size.height - ((price - minPrice) / priceRange * size.height).toFloat()
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw last point dot
            val lastX = (priceHistory.size - 1) * stepX
            val lastY = size.height - ((priceHistory.last() - minPrice) / priceRange * size.height).toFloat()
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(lastX, lastY)
            )
        }
    }
}

@Composable
private fun MerchantInfo(merchantName: String, score: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Satıcı",
                fontSize = 12.sp,
                color = MediumGray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = merchantName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
        }
        // Score badge
        Text(
            text = "$score",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BorsaDarkGreen,
            modifier = Modifier
                .background(BorsaDarkGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SepeteEkleBar(product: Product) {
    val priceColor by animateColorAsState(
        targetValue = if (product.isPriceUp) BorsaGreen else BorsaRed,
        animationSpec = tween(300),
        label = "bottomPriceColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "%.2f TL".format(product.currentPrice),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = priceColor
            )
        }
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = TrendyolOrange
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Sepete Ekle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
