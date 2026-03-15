# Climora 🌤️

A feature-rich Android weather application that provides real-time updates, multi-location tracking, and personalized weather alerts. Built with modern Android development best practices.

## 🚀 Project Overview
WeatherWise allows users to stay informed about weather conditions across the globe. Whether you prefer GPS-based location detection or selecting a spot on an interactive map, this app gives you full control over your weather data.

## 📱 Key Features
* **Dynamic Weather Data:** Real-time metrics including temperature, humidity, wind speed, pressure, and cloud coverage.
* **Flexible Location Selection:** Choose between GPS-based location or manual selection via an interactive Google Map with search auto-complete.
* **Customizable Settings:** * Temperature: Celsius, Fahrenheit, Kelvin
    * Wind Speed: m/s, mph
    * Language support: English, Arabic
* **Favorites System:** Save your favorite locations for quick access to detailed 5-day forecasts.
* **Smart Alerts:** Set custom notifications or alarms based on specific weather conditions (e.g., rain, extreme temperatures).

## 🛠️ Technical Stack
The app is built using modern Android development standards:

* **Architecture:** MVVM (Model-View-ViewModel) to ensure separation of concerns and testability.
* **Networking:** [Retrofit](https://square.github.io/retrofit/) for consuming weather APIs.
* **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) for caching favorite locations and weather data offline.
* **Asynchronous Processing:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) for clean, reactive data streams.
* **Background Tasks:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for handling reliable weather alerts and background sync.
* **Maps/Location:** Google Maps SDK & FusedLocationProviderClient.

## 🧪 Quality Assurance
* **Robustness:** Stability is maintained through strict data handling and proper state management.
* **Testing:** Comprehensive **Unit Testing** implemented to verify logic in ViewModels and Data Repositories, ensuring app accuracy and reliability.

## ⚙️ Setup
1. Clone the repository.
2. Add your API Key from [OpenWeatherMap](https://openweathermap.org/) (or your chosen provider) in the `local.properties` file or your `build.gradle` (using `BuildConfig`).
3. Ensure your Google Maps API Key is configured in `AndroidManifest.xml`.
4. Build and run on an Android Emulator or physical device.

## 💡 Acknowledgements
This project was developed as part of ITI Kotlin Course.
