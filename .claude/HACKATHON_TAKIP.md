# 📱 TrendBorsa: Android (Native) Mimari ve Geliştirme Yol Haritası

Bu doküman, TrendBorsa projesinin Android uygulamasının **Google Recommended Architecture**, **MVVM**, ve **SOLID** prensiplerine sadık kalınarak nasıl inşa edileceğini açıklar. Gerçek zamanlı borsa deneyimini kusursuz sunmak için WebSocket yönetimi ve StateFlow mimarisi merkeze alınmıştır.

---

## 1. 🏛️ Mimari Katmanlar (Clean Architecture & MVVM)

Proje klasik 3 katmanlı mimari üzerine inşa edilecektir:

### A. UI Layer (Kullanıcı Arayüzü Katmanı)
* **Görev:** Ekranda veriyi göstermek ve kullanıcı etkileşimlerini yakalamak.
* **Bileşenler:** Jetpack Compose (Ekranlar) ve `ViewModel`.
* **Kural (Single Responsibility):** Compose fonksiyonları asla iş mantığı (business logic) içermez. Sadece ViewModel'dan gelen `StateFlow`'u dinler (`collectAsState()`) ve kullanıcı tıklamalarını ViewModel'a olay (Event/Intent) olarak iletir.

### B. Domain Layer (İş Mantığı Katmanı - İsteğe Bağlı ama Önerilen)
* **Görev:** Karmaşık iş kurallarını UI ve Data katmanından soyutlamak.
* **Bileşenler:** `UseCase` (veya Interactor) sınıfları.
* **Örnek:** `CalculatePriceTrendUseCase`, gelen yeni fiyatın eskisinden yüksek mi düşük mü olduğunu hesaplayıp UI'a "yeşil" veya "kırmızı" yanma bilgisini hazırlar.

### C. Data Layer (Veri Katmanı)
* **Görev:** Verinin nereden geleceğine (Network, WebSocket, Local DB) karar vermek.
* **Bileşenler:** `Repository` sınıfları ve `DataSource`'lar.
* **Kural (Dependency Inversion):** ViewModel doğrudan Retrofit veya OkHttp kullanmaz. Sadece `IPriceRepository` arayüzünü (interface) tanır. Gerçek veri çekme işlemi bu arayüzü implemente eden sınıflarda yapılır.

---

## 2. 🛠️ Tech Stack ve Kütüphaneler

* **UI:** Jetpack Compose (Modern, deklaratif UI).
* **Mimari:** ViewModel, LiveData/StateFlow, Hilt (Dependency Injection için - SOLID'in D harfi için şarttır).
* **Ağ & Gerçek Zamanlı:** Retrofit (REST API), OkHttp (REST ve WebSocket).
* **Asenkron Programlama:** Kotlin Coroutines & Flow.
* **Grafik:** Vico veya YCharts (Jetpack Compose uyumlu borsa grafikleri için).

---

## 3. 🔌 WebSocket Best Practice'leri (Kritik Noktalar)

Android'in katı yaşam döngüsü (Lifecycle) nedeniyle WebSocket yönetimi özen ister. Açık unutulan bir soket pili tüketir ve işletim sistemi tarafından acımasızca öldürülür.

1.  **Lifecycle Awareness (Yaşam Döngüsü Farkındalığı):**
    * Kullanıcı uygulamayı arka plana attığında (örn: `onPause` veya `onStop`) WebSocket bağlantısını kesin.
    * Uygulamaya geri döndüğünde (`onResume` veya `onStart`) bağlantıyı tekrar kurun.
    * *Uygulama:* Jetpack Compose'da `LifecycleEventObserver` veya ViewModel içinde `viewModelScope` ile bu yönetimi sağlayın.
2.  **Callback'leri Flow'a Çevirme (`callbackFlow`):**
    * OkHttp'nin WebSocket listener'ı callback tabanlıdır. Bunu Clean Code'a uydurmak için Kotlin `callbackFlow` kullanarak bir `SharedFlow` veya `StateFlow`'a dönüştürün. Böylece Repository katmanınız ViewModel'a temiz bir veri akışı (stream) sunar.
3.  **Auto-Reconnect ve Exponential Backoff:**
    * Bağlantı koptuğunda (internet gitmesi, sunucu resetlenmesi) hemen tekrar bağlanmak yerine "Exponential Backoff" (örn: 1sn, 2sn, 4sn, 8sn bekle ve tekrar dene) algoritması kullanın.
4.  **Ping/Pong (Heartbeat):**
    * OkHttp builder'ında `pingInterval` değerini ayarlayarak bağlantının canlı kalmasını sağlayın (Örn: `pingInterval(15, TimeUnit.SECONDS)`).

---

## 4. 🧩 SOLID Uygulama Örnekleri

* **S (Single Responsibility):** `WebSocketClient` sınıfı sadece bağlantıyı açar/kapatır ve mesaj alır. Gelen JSON mesajını parse etmek veya ekranda nasıl görüneceğine karar vermek onun işi değildir.
* **O (Open/Closed):** Grafik çizeceğiniz kütüphaneyi doğrudan koda gömmek yerine, bir arayüz (`ChartRenderer`) yazın. Yarın Vico yerine MPAndroidChart kullanmak isterseniz ana kodunuzu değiştirmeden yeni bir sınıf ekleyerek çözersiniz.
* **D (Dependency Inversion):** Hilt kullanarak sınıfların bağımlılıklarını dışarıdan enjekte edin. (Örn: `ProductViewModel` constructor'ında `IProductRepository` beklesin. Böylece test yazarken veya fake data kullanırken gerçek sunucuya bağlanmak zorunda kalmazsınız).

---

## 5. 🚀 Geliştirme Fazları (İş Bölümü Kurgusu)

Eğer Android tarafında iki kişi çalışıyorsanız:

### 👤 Geliştirici 1: "UI & State Master"
* Figma/Tasarıma bakarak Jetpack Compose ile `ProductDetailScreen` arayüzünü inşa eder.
* State'leri (Loading, Success, Error) ViewModel'dan `collectAsStateWithLifecycle()` kullanarak dinler.
* Fiyat değişimlerindeki konfeti ve renk animasyonlarını (Lottie / Compose Animations) entegre eder.

### 👤 Geliştirici 2: "Data & Network Architect"
* Hilt kurulumunu yapar ve modülleri (NetworkModule, RepositoryModule) tanımlar.
* Retrofit ile `/api/buy` endpoint'ini bağlar.
* `OkHttp` ile WebSocket Client'ı yazar, `callbackFlow` kullanarak gelen anlık fiyatları `ProductRepository` üzerinden UI katmanına iletir.