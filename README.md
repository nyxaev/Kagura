# Kagura

Kagura is an Android application built with Jetpack Compose, Material 3, Hilt, Room, and Coroutines. It features anime, manga, and movie catalog exploration, gacha-based random media selection, real-time search with AniList and TMDB APIs, and robust local persistence.

## Run Instructions
1. Clone the repository in Android Studio.
2. Configure your TMDB API key in `local.properties` as `TMDB_API_KEY=your_api_key_here`.
3. Sync project with Gradle files.
4. Run the app on an Android emulator or physical device.

## Architecture
- Clean Architecture with MVVM pattern
- Dependency Injection with Hilt
- Reactive UI flows using Kotlin Coroutines and StateFlow
- Local database management using Room with FTS search
- Remote integration using Retrofit for AniList and TMDB APIs

## Video Script (30-45 seconds)
[0:00 - 0:10] Opening screen showing the harmonious dark theme and custom typography. Tapping into the Search screen to demonstrate real-time debounced search with category filters and offline fallback.
[0:10 - 0:25] Navigating to the Gacha screen ("Filme da Vez"). Selecting genre chips, setting tear meter, tapping "Escolher" with haptic feedback and animated capsule reveal.
[0:25 - 0:40] Reviewing result actions ("Sortear de novo", "Vamos com esse!", and sharing via Intent). Concluding with smooth navigation across catalog and profile views.
