# TrendBorsa - Hackathon Takip

## Analiz Ozeti

| Kriter | Puan | Yorum |
|---|---|---|
| Inovasyon | 7/10 | Bilinen konseptlerin yeni kombinasyonu, kulturel fit guclu |
| Tamamlanma | 3/10 | Dokumantasyon iyi, calisan prototip yok |
| Entegrasyon eforu | Yuksek | Fiyat motoru + websocket + concurrency = ciddi backend |
| Sunum etkisi | 7/10 | Hikaye guclu, karsilastirmali analiz iyi, somut data/prototip eksik |
| Sorulara hazirlik | 5/10 | Konseptsel sorulara evet, teknik/finansal sorulara zayif |

---

## Guclu Yanlar
- Problem-cozum iliskisi net
- Psikolojik analiz derin (FOMO, dip avciligi, social proof)
- Puanlama matrisi ile karsilastirmali degerlendirme
- Risk/dezavantaj analizi dengeli
- MVP scope net (5 madde)
- Hibrit model onerisi (TrendExchange) derinlik gosteriyor

## Zayif Yanlar
- Somut benchmark data yok (StockX metrikleri vs.)
- Fiyat algoritmasi formule edilmemis
- Prototip/wireframe/mockup yok
- Gelir projeksiyonu/simulasyon yok
- Puanlama self-scoring bias tasiyor

## Sorulara Hazirlik Durumu

| Muhtemel Soru | Hazir? | Aksiyon |
|---|---|---|
| Algoritma nasil calisacak? | ❌ | Formul + ornek hesaplama hazirla |
| Yasal risk nasil yonetilecek? | ⚠️ | Somut adimlar belirle |
| Kanibalizasyon? | ✅ | "Fiyat artabilir de" argumani yeterli |
| StockX'ten farki? | ⚠️ | Direkt karsilastirma tablosu hazirla |
| ROI ne? | ❌ | Basit senaryo simulasyonu yap |
| Demo? | ❌ | MVP prototip gerek |

---

## Entegrasyon Efor Analizi

### Backend (Yuksek)
- [ ] Real-time fiyat motoru (event-driven, Kafka/Redis Streams)
- [ ] Websocket altyapisi (canli fiyat push)
- [ ] Concurrency/race condition yonetimi (optimistic locking + queue)
- [ ] Tavan/taban limit + fiyat koruma + iade logic
- [ ] Algoritma parametreleri + A/B test altyapisi

### Data (Orta-Yuksek)
- [ ] Yeni event stream (alim, fiyat degisimi, izleyici)
- [ ] Time-series DB (fiyat gecmisi)
- [ ] Analitik (peak analizi, kullanici clustering, ML model)

### Frontend/Mobile (Orta)
- [ ] Canli grafik component (mum/cizgi, websocket)
- [ ] Ticker animasyonlari (yesil/kirmizi)
- [ ] Widget gelistirme

---

## Yapilacaklar (Hackathon Oncesi)

- [ ] Fiyat algoritmasi formulize et (talep katsayisi, zaman penceresi, tavan/taban)
- [ ] Ornek hesaplama senaryosu hazirla (10 alim -> fiyat X'ten Y'ye)
- [ ] StockX / Uber surge karsilastirma tablosu
- [ ] Basit gelir simulasyonu (1000 kullanici, 50 urun, 1 hafta)
- [ ] UI wireframe / mockup (en az 3 ekran)
- [ ] Calisan MVP demo (mock data ile bile olsa)
- [ ] Sunum slide'lari hazirla

---

## Notlar

_Bu dosya hackathon surecinde guncellenecek._
