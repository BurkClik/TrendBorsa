package com.example.trendborsa.data.mock

import com.example.trendborsa.domain.model.Product

object MockProductDataSource {

    fun getProduct(): Product = Product(
        id = 974364895,
        name = "Coconut Mix - 250 ml",
        brandName = "MOMORDİCA",
        images = listOf(
            "https://cdn.dsmcdn.com/ty1000032/product/media/images/prod/PIM/20251106/14/dadfe838-2bf9-4532-8f72-cb67720fafa8/1_org_zoom.jpg",
            "https://cdn.dsmcdn.com/ty1000031/product/media/images/prod/PIM/20251106/14/4c0fdc69-421a-456a-9747-97d485f68d97/1_org_zoom.jpg",
            "https://cdn.dsmcdn.com/ty1747/product/media/images/prod/PIM/20250930/18/ac97fd34-0852-42a7-9827-5975fb855244/1_org_zoom.jpg",
            "https://cdn.dsmcdn.com/ty1000058/product/media/images/prod/PIM/20251228/23/c97dd5aa-43eb-4886-81e6-8086fa52f41a/1_org_zoom.jpg",
            "https://cdn.dsmcdn.com/ty1000059/product/media/images/prod/PIM/20251228/23/7083005f-a0a8-42ad-960a-9ff239e4bbeb/1_org_zoom.jpg",
            "https://cdn.dsmcdn.com/ty1745/product/media/images/prod/PIM/20250923/11/505cabf7-5dfa-4ee2-a3ac-5af8a364990f/1_org_zoom.jpg"
        ),
        favoriteCount = "539B",
        categoryRanking = "En Çok Satan 1. Ürün",
        categoryName = "Fonksiyonel İçecek Kategorisinde",
        categoryHierarchy = "Süpermarket/Gıda & İçecek/Gazsız İçecek/Fonksiyonel İçecek",
        marketPrice = 589.0,
        salePrice = 175.0,
        currentPrice = 175.0,
        discountPercentage = "-%70",
        merchantName = "Momordica",
        merchantScore = 8.7,
        stockQuantity = 18207,
        ratingText = "21.176 Değerlendirme",
        priceChangePercent = 0.0,
        isPriceUp = true,
        priceHistory = listOf(175.0)
    )
}
