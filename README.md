# 📋 SkyWear: 🇰🇷🇯🇵 KR-JP Smart Travel Outfit Coordinator
> **"Don't just check the weather. Know what to wear."**
>
> **SkyWear** [Sky (Weather) + Wear (Outfit)]: An Android-native solution designed to bridge the temperature gap between Korea and Japan, transforming raw weather data into actionable, culturally-aware travel outfit recommendations.

An Android travel companion that compares real-time and 5-day forecast weather between Korean and Japanese cities, delivers 8-stage outfit recommendations powered by Wind Chill and Heat Index algorithms, and adapts entirely to the traveler's direction — whether heading from Korea to Japan, or Japan to Korea. Lifecycle features including city subscription, seasonal recommendations, and a travel journal keep users engaged long after the trip ends.

---

## 🎯 Background & Motivation

### The Context: "Seamless Travel Experience"
Korea and Japan are the closest neighboring countries in Northeast Asia, yet their climates differ significantly due to latitude, terrain, and seasonal patterns. It is common for Seoul to be below freezing while Osaka remains comfortably above 10°C — a gap that can make or break a traveler's packing decision.

### The Problem
1. **Cross-reference Fatigue**: Travelers manually switch between Korean and Japanese weather apps, mentally calculating temperature differences without any outfit context.
2. **Numbers Without Meaning**: "8°C" alone doesn't tell a traveler whether they need a light coat or a heavy parka. Raw data lacks the outfit intelligence layer.
3. **One-Directional Design**: Most travel apps are built for a single market. There is no solution that seamlessly serves both Korean travelers heading to Japan *and* Japanese travelers heading to Korea.
4. **No Long-term Value**: Most weather apps are forgotten after a trip ends. There is no reason to return unless another trip is planned.

### The Solution
1. **Dual-City Dashboard**: Displays real-time and 5-day forecast weather for both departure and destination cities side-by-side, with daily min/max temperature and temperature gap visualization.
2. **8-Stage Outfit Engine**: Converts temperature into concrete outfit stages — from T-shirt + Shorts (28°C+) to Long Puffer + Scarf + Hand Warmers (below -1°C) — enhanced by Wind Chill and Heat Index corrections.
3. **Bidirectional Travel Mode**: A single toggle switches the entire app experience between KR→JP and JP→KR, including localized checklists, comparison messages, and outfit advice.
4. **Lifecycle Features**: City subscription, seasonal recommendations, and travel journal ensure users return even when not actively planning a trip.

- **Data Source**: OpenWeatherMap API (Current Weather + 5-Day Forecast)
- **Key Features**:
  1. **Dual-City Weather Comparison**: Side-by-side KR/JP weather cards with feels-like temperature and humidity
  2. **8-Stage Outfit Algorithm**: Temperature-to-outfit mapping with Wind Chill / Heat Index corrections
  3. **5-Day Forecast with Date Selector**: Daily min/max temperature across all time slots per day
  4. **Travel Direction Switch**: KR→JP ↔ JP→KR bidirectional toggle with full UX adaptation
  5. **Localized Checklist**: Direction-aware travel checklist (Japan trip / Korea trip) in 3 languages
  6. **City Subscription**: Follow up to 5 cities with real-time weather and alert toggle
  7. **Seasonal Recommendations**: D-day countdown for 8 JP + 8 KR major travel events, direction-aware
  8. **Travel Journal**: Record trip weather, outfit history, and personal memos
  9. **Bring-along Items**: Destination-based extras (umbrella only on rainy codes, hand warmer, windbreaker, ...) shown on the dashboard
  10. **Notifications**: Daily 08:00 travel briefing (direction-aware) and hourly ±3°C change alerts for subscribed cities, with Android 13+ permission handling
  11. **Full i18n**: Korean / English / Japanese localization across all screens, notifications, and error messages
  12. **Responsive Layout & Dark Mode**: Adaptive padding/typography and contrast-checked dark theme colors
  13. **Firebase Crashlytics**: Production crash monitoring with release-only collection

---

## ⚙️ Key Features

- **Outfit Intelligence**: Goes beyond temperature numbers to deliver stage-based outfit recommendations tailored to real feels-like conditions
- **Bidirectional UX**: Full experience swap — departure/destination cities, comparison messages, travel advice, checklists, and seasonal recommendations — with a single toggle
- **Long-term Engagement**: City subscription, travel journal, and seasonal events keep users returning between trips
- **Multilingual Support**: Complete localization in Korean, English, and Japanese including weather descriptions via dynamic API `lang` parameter
- **Responsive Design**: Adaptive layout with proportional spacing and typography across all Android devices

---

## 🛠️ Tech Stack

| Category | Stack |
| --- | --- |
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) ![Coroutines](https://img.shields.io/badge/Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) ![Flow](https://img.shields.io/badge/Flow-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) |
| **UI** | ![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) ![Material 3](https://img.shields.io/badge/Material_3-757575?style=for-the-badge&logo=materialdesign&logoColor=white) ![Navigation](https://img.shields.io/badge/Navigation-3DDC84?style=for-the-badge&logo=android&logoColor=white) |
| **Architecture** | ![MVVM](https://img.shields.io/badge/MVVM-2E7D32?style=for-the-badge) ![Hilt](https://img.shields.io/badge/Hilt-E91E63?style=for-the-badge&logo=android&logoColor=white) |
| **Network** | ![Retrofit](https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white) ![OkHttp](https://img.shields.io/badge/OkHttp-48B983?style=for-the-badge&logo=square&logoColor=white) ![Gson](https://img.shields.io/badge/Gson-4285F4?style=for-the-badge&logo=google&logoColor=white) |
| **Local Storage** | ![Room](https://img.shields.io/badge/Room-003B57?style=for-the-badge&logo=sqlite&logoColor=white) ![DataStore](https://img.shields.io/badge/DataStore-3DDC84?style=for-the-badge&logo=android&logoColor=white) |
| **Background** | ![WorkManager](https://img.shields.io/badge/WorkManager-3DDC84?style=for-the-badge&logo=android&logoColor=white) |
| **Monitoring** | ![Firebase Crashlytics](https://img.shields.io/badge/Firebase_Crashlytics-FFCA28?style=for-the-badge&logo=firebase&logoColor=black) |
| **Testing** | ![JUnit4](https://img.shields.io/badge/JUnit4-25A162?style=for-the-badge&logo=junit5&logoColor=white) |
| **API** | ![OpenWeatherMap](https://img.shields.io/badge/OpenWeatherMap-EB6E4B?style=for-the-badge) |

<details>
<summary>📦 Versions</summary>

| Item | Version |
| --- | --- |
| Kotlin / KSP | `2.0.21 / 2.0.21-1.0.28` |
| Android Gradle Plugin / JDK | `8.9.1 / 21` |
| minSdk / targetSdk | `26 / 36` |
| Compose BOM / Material 3 | `2025.10.00 / 1.4.0` |
| Navigation Compose | `2.8.9` |
| Hilt / hilt-work | `2.51.1 / 1.2.0` |
| Retrofit / OkHttp | `2.11.0 / 4.12.0` |
| Room (DB schema) | `2.7.1 (v3)` |
| DataStore / WorkManager | `1.1.1 / 2.9.1` |
| Firebase BOM | `33.1.0` |

</details>

---

## 🚀 Getting Started

1. Install JDK 21. Gradle picks it up via `gradle/gradle-daemon-jvm.properties`; in Android Studio, set *Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK* to a JDK 21 (e.g. the bundled JetBrains Runtime).
2. Get a free API key from [OpenWeatherMap](https://openweathermap.org/api).
3. Add it to `SkyWear/local.properties` (exposed as `BuildConfig.WEATHER_API_KEY`):
   ```properties
   WEATHER_API_KEY=your_api_key_here
   ```
4. Place your Firebase `google-services.json` in `SkyWear/app/` (required by the Google Services / Crashlytics plugins).
5. Build and test:
   ```bash
   cd SkyWear
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ```

---

## 🏗️ Architecture

```mermaid
graph TD
    subgraph UI["🖥️ UI Layer (Jetpack Compose)"]
        MA[MainActivity]
        NAV[SkyWearNavGraph\nBottom Navigation]
        WVM[WeatherViewModel]
        CVM[ChecklistViewModel]
        CSVM[CitySearchViewModel]
        SUBVM[SubscribeViewModel]
        JVM[JournalViewModel]
    end

    subgraph Domain["🧠 Domain Layer"]
        OA[OutfitAlgorithm]
        TC[TemperatureComparator]
        CA[ContextAwareRecommendation]
        SE[SeasonEvent]
    end

    subgraph Data["🗄️ Data Layer"]
        WR[WeatherRepository]
        CR[ChecklistRepository]
        UPR[UserPreferencesRepository]
        SR[SubscribeRepository]
        JR[JournalRepository]
    end

    subgraph Background["⏰ Background (WorkManager + Hilt)"]
        WNW[WeatherNotificationWorker\nDaily 08:00 Briefing]
        SWW[SubscribeWeatherWorker\nHourly ±3°C Alert]
    end

    subgraph Remote["🌐 Remote"]
        API[WeatherApiService\nRetrofit2 + OkHttp3]
        OWM[(OpenWeatherMap API)]
    end

    subgraph Local["💾 Local"]
        ROOM[(Room DB v3\nChecklist + Subscribe + Journal)]
        DS[(DataStore\nPreferences)]
    end

    MA --> NAV --> WVM & CVM & CSVM & SUBVM & JVM
    WVM --> WR & OA & TC & CA
    SUBVM --> SR & WR
    JVM --> JR
    CVM --> CR & UPR
    CSVM --> UPR
    WNW --> WR & UPR & OA
    SWW --> WR & SR
    WR --> API --> OWM
    CR --> ROOM
    SR --> ROOM
    JR --> ROOM
    UPR --> DS
```

---

## ✅ Milestone

- **Phase 1**: Project Foundation & Android Environment Setup
  - [x] Phase 1-1: Initialize GitHub Repository & Technical Documentation (README.md) & Project Board
  - [x] Phase 1-2: Setup Android Studio & Kotlin/Compose Development Environment
  - [x] Phase 1-3: Define Design System (Color Palette, Typography, & Brand Assets)
  - [x] Phase 1-4: Security Configuration (API Key Management & local.properties Setup)

- **Phase 2**: Network Layer & Weather Data Integration
  - [x] Phase 2-1: Architect Remote Data Source using Retrofit2 & OkHttp3
  - [x] Phase 2-2: Design Weather Data Transfer Objects (DTO) for OpenWeatherMap API
  - [x] Phase 2-3: Implement Dual-City Weather Fetching Logic (Source: KR / Destination: JP)
  - [x] Phase 2-4: Build Robust Error Handling & Interceptor for Network Stability

- **Phase 3**: Core Logic & Outfit Recommendation Engine
  - [x] Phase 3-1: Develop Temperature-based 8-Stage Smart Outfit Algorithm
  - [x] Phase 3-2: Implement Comparative Analysis Logic (KR vs JP Temperature Gap)
  - [x] Phase 3-3: Build Context-Aware Recommendation Logic (Wind Chill & Humidity)
  - [x] Phase 3-4: Reactive State Management Integration using ViewModel & StateFlow
  - [x] Phase 3-5: Design Asset Mapping Engine (Weather State to Visual Icons) — *removed in Phase 10 as no screen rendered it*

- **Phase 4**: Travel Intelligence & Data Persistence
  - [x] Phase 4-1: Implement Direction-Aware Travel Checklist using Room DB
  - [x] Phase 4-2: Develop City Search & User Preference Management Features
  - [x] Phase 4-3: Build Background Notification Service for Daily Travel Briefing
  - [x] Phase 4-4: Dark Mode Support

- **Phase 5**: Quality Assurance & Dependency Injection
  - [x] Phase 5-1: Execute UI Testing & Component Validation using Compose Test Rule — *replaced by domain/data unit tests in Phase 10*
  - [x] Phase 5-2: Code Refactoring & Dependency Injection (Hilt) Optimization
  - [x] Phase 5-3: Final Build Generation (.APK)

- **Phase 6**: Main UI Implementation
  - [x] Phase 6-1: Navigation & Screen Integration
  - [x] Phase 6-2: Dual-City Weather Dashboard Screen
  - [x] Phase 6-3: City Search Screen
  - [x] Phase 6-4: Direction-Aware Travel Checklist Screen
  - [x] Phase 6-5: Bug Fixes & Code Quality Improvements

- **Phase 7**: Product Optimization
  - [x] Phase 7-1: Advanced Localization (Korean / English / Japanese)
  - [x] Phase 7-2: Travel Direction Switch (KR→JP ↔ JP→KR)
  - [x] Phase 7-3: Performance & Monitoring (Firebase Crashlytics, StrictMode)

- **Phase 8**: 5-Day Forecast Comparison
  - [x] Phase 8-1: Forecast API Integration & DTO (WeatherForecastResponse, DailyForecastPair)
  - [x] Phase 8-2: Forecast Dashboard UI with Date Selector & Daily Min/Max Temperature
  - [x] Phase 8-3: Project Retrospective & Feedback
  - [x] Phase 8-4: Technical Documentation

- **Phase 9**: Lifecycle Features & Long-term Engagement
  - [x] Phase 9-1: Bottom Navigation Bar Integration (Weather / Subscribe / Journal / Season)
  - [x] Phase 9-2: City Subscription & Weather Change Alerts (up to 5 cities)
  - [x] Phase 9-3: Seasonal Travel Recommendation Engine (8 JP + 8 KR events, direction-aware)
  - [x] Phase 9-4: Travel Journal & Weather History Log
  - [x] Phase 9-5: Portfolio Finalization & README Update
  - [x] Phase 9-6: Responsive Layout Optimization for all screen sizes

- **Phase 10**: Code Quality & UX Refinement
  - [x] Phase 10-1: Remove dead code, unused assets (Lottie, template files) and stale dependencies
  - [x] Phase 10-2: Android 13+ notification permission, tappable notifications, localized notifications & error messages
  - [x] Phase 10-3: UX fixes — checklist default items, journal date pickers & input validation, dark mode contrast, bring-along items
  - [x] Phase 10-4: Dependency alignment (Compose BOM 2025.10, Room 2.7, lifecycle 2.9), lint cleanup, 45 unit tests
  - [x] Phase 10-5: Review fixes — forecast in local time, stale request cancellation, coroutine cancellation handling, portable Gradle config, 50 unit tests

---

## 🔥 Troubleshooting & Lessons Learned

**1. KSP + Kotlin Version Compatibility**
- **Challenge**: The initial Kotlin 2.2.10 + KSP 2.2.10-2.0.2 setup failed to build with the Room compiler and the rest of the toolchain.
- **Resolution**: Aligned the toolchain to Kotlin `2.0.21` with the matching KSP `2.0.21-1.0.28` — the KSP version prefix must always equal the Kotlin version.

**2. Hilt — Missing `android:name` in AndroidManifest**
- **Challenge**: App crashed on launch with `Hilt Activity must be attached to an @HiltAndroidApp Application`.
- **Resolution**: Added `android:name=".SkyWearApplication"` to `<application>` tag in `AndroidManifest.xml`.

**3. ViewModel Instance Isolation Across Screens**
- **Challenge**: City selection in SearchScreen was not reflected in DashboardScreen — each screen created its own ViewModel instance via `hiltViewModel()`.
- **Resolution**: Elevated `hiltViewModel()` calls to NavGraph level and passed shared instances as parameters to each screen, ensuring a single source of truth.

**4. Travel Direction — Inverted Gap Degree Logic**
- **Challenge**: When direction was JP→KR and the destination (Korea) was colder, the app incorrectly advised "dress one layer lighter."
- **Root Cause**: `gapDegree` is always calculated as `jpTemp - krTemp`. The sign needed to be flipped for JP→KR direction to correctly represent the destination temperature delta.
- **Resolution**: Applied `directedGap = if (isKrToJp) gapDegree else -gapDegree` throughout comparison and advice logic.

**5. Compose i18n — `stringResource()` Context Constraint**
- **Challenge**: String generation for outfit recommendations and comparison messages was embedded in the Domain layer, making localization impossible since `stringResource()` requires a Composable context.
- **Resolution**: Removed all string generation from Domain layer. Created dedicated `@Composable` functions in the UI layer that call `stringResource()` directly.

**6. Room TypeConverter — Enum Persistence**
- **Challenge**: Room DB had no mechanism to store `ChecklistCategory` enum, risking a runtime crash on first checklist access.
- **Resolution**: Created `ChecklistConverters` class with `@TypeConverter` annotations for bidirectional `String ↔ ChecklistCategory` conversion.

**7. Dynamic City Name Localization**
- **Challenge**: OpenWeatherMap API always returns city names in English regardless of the `lang` parameter. Display names remained in English even when device language was set to Japanese.
- **Resolution**: Built `localizedCityName(nameEn: String)` lookup function in `CitySearchData.kt` that maps English API city names to native equivalents using a predefined city list.

**8. Forecast Date Filtering**
- **Challenge**: Past dates remained visible in forecast date selector tabs after midnight.
- **Resolution**: Added `.filter { it >= today }` with a renamed lambda parameter (`key`) to avoid shadowing the `dateKey()` extension function. "Today" is now computed in the city's time zone (see #17).

**9. Daily Min/Max Temperature Reliability**
- **Challenge**: A single 12:00 forecast slot showed high variability — a day might have a warm afternoon but a cold evening, making outfit advice misleading.
- **Resolution**: Extracted `minOf` and `maxOf` across all available 3-hour slots per day using `dailyRepresentativeWithTime()`, returning a `Triple` of (representative slot, time, min/max pair).

**10. Room DB Schema Evolution**
- **Challenge**: Adding `SubscribedCity` and `TravelJournal` entities required a safe migration path without data loss.
- **Resolution**: Implemented `MIGRATION_2_3` with explicit `CREATE TABLE IF NOT EXISTS` SQL statements, maintaining clean incremental migration from v1 → v2 → v3.

**11. Responsive Layout — White Space on Larger Screens**
- **Challenge**: Fixed padding (16dp) and `displayMedium` font caused excessive white space on larger phones.
- **Resolution**: Reduced vertical padding to 8dp, switched temperature display to `displaySmall`, and tightened card spacing from 16dp to 10dp across all screens.

**12. Silent Notifications on Android 13+**
- **Challenge**: `POST_NOTIFICATIONS` was declared in the manifest but never requested, so both workers ran successfully while every notification was silently dropped (targetSdk 36).
- **Resolution**: Request the runtime permission once on first launch, and attach a `PendingIntent` so tapping a notification opens the app.

**13. OkHttp Interceptor Connection Leak**
- **Challenge**: `NetworkInterceptor` threw a custom exception on non-200 responses without closing the response body, leaking connections.
- **Resolution**: Return any 2xx as-is, `close()` the response before mapping the status code to a `NetworkException`, and map exceptions to localized string resources in the UI.

**14. `stateIn` Initial Value Trap**
- **Challenge**: `SubscribeViewModel` refreshed weather in `init` by reading `subscribedCities.value`, which is always the empty initial value of `stateIn(WhileSubscribed)` — no weather loaded until the user tapped refresh.
- **Resolution**: Read the source flow directly with `repository.getAllCities().first()`.

**15. Duplicate API Calls from Screen-scoped ViewModels**
- **Challenge**: The Season tab called `hiltViewModel()` for its own `WeatherViewModel`, re-running 4 weather/forecast requests every time the tab opened just to read the travel direction.
- **Resolution**: Pass the NavGraph-level shared instance (same lesson as #3).

**16. Checklist Defaults Deleted Permanently**
- **Challenge**: "Delete checked items" also removed seeded default items, which are only inserted in `RoomDatabase.Callback.onCreate` and could not be restored.
- **Resolution**: Delete only custom items and uncheck defaults in a single `@Transaction`, behind a confirmation dialog.

**17. Forecast Shifted by 9 Hours (UTC `dt_txt`)**
- **Challenge**: The forecast API's `dt_txt` is UTC, but it was parsed as local time. In KR/JP (UTC+9) the "12:00" representative slot was really 21:00 and each day was split at 09:00 local.
- **Resolution**: Derive date key, date label and time label from `dt` plus the city's `timezone` offset with `java.time`, and round daily min/max instead of truncating.

**18. Stale Responses Overwriting New Ones**
- **Challenge**: Each refresh or city change launched new coroutines without cancelling the old ones, so a slower earlier request could overwrite the dashboard with the previous city.
- **Resolution**: Keep the in-flight `Job`s and cancel them before fetching again; reset the selected forecast date when it disappears from the new result.

**19. `runCatching` Swallowing Cancellation**
- **Challenge**: `runCatching` and `catch (e: Exception)` also caught `CancellationException`, turning cancelled requests into error states and stopped workers into retries.
- **Resolution**: Wrap API calls with `apiResult`, which rethrows cancellation, and fetch KR/JP in parallel with `async` as the code comments intended.

**20. Machine-specific `org.gradle.java.home`**
- **Challenge**: `gradle.properties` pinned the JDK to a local Android Studio path, so the project could not build on another machine or CI.
- **Resolution**: Remove it and rely on `gradle-daemon-jvm.properties` (JDK 21) and `jvmToolchain(21)`.

---

## 📈 Results

- **Localization**: 100% string coverage in Korean, English, and Japanese across all screens including Subscribe, Journal, and Season screens
- **Bidirectional Support**: Full UX adaptation for both KR→JP and JP→KR travel modes including direction-aware seasonal recommendations
- **Forecast Accuracy**: Daily min/max temperature extracted from all available 3-hour slots — not just a single time slot
- **Long-term Engagement**: 4-tab lifecycle loop (Weather → Subscribe → Journal → Season)
- **Responsive Design**: Consistent layout across small (360dp) to large (420dp+) Android screens
- **Crash Monitoring**: Firebase Crashlytics integrated with release-only collection policy
- **Error Handling**: 7-type sealed class network errors mapped to localized user-facing messages
- **Architecture**: 3-layer separation (Data / Domain / UI) with Room DB v3 and clean migration path
- **Quality**: 50 JVM unit tests (outfit engine, feels-like correction, forecast aggregation, city search, input validation) and 0 Android Lint errors

---

## 🧐 Self-Reflection

### Technical Growth
- **Algorithm to Product**: Translating meteorological formulas (Wind Chill, Heat Index) into a user-facing outfit recommendation system taught me the importance of bridging domain knowledge with engineering.
- **State Management Mastery**: Managing bidirectional travel state across multiple screens with `StateFlow`, `flatMapLatest`, and shared ViewModels deepened my understanding of reactive architecture in Jetpack Compose.
- **i18n Architecture**: Learning that internationalization must be a first-class architectural concern — not an afterthought — was one of the most valuable lessons of this project.
- **DB Migration Strategy**: Incremental Room migrations (`MIGRATION_1_2`, `MIGRATION_2_3`) demonstrated the importance of planning schema evolution from the start.

### Problem-Solving Mindset
- **Cultural Product Thinking**: Building for both Korean and Japanese travelers reinforced that great products require empathy for diverse user contexts, not just technical correctness.
- **Root Cause Over Quick Fix**: The gapDegree sign inversion bug could have been patched with a conditional, but understanding *why* the sign was wrong — and fixing the abstraction — produced a cleaner, more maintainable solution.
- **Layer Discipline**: Every time a string or logic leaked between layers, it created downstream i18n or testability problems. Respecting architectural boundaries is not academic — it has real engineering consequences.
- **User Lifecycle Thinking**: Realizing that "weather app for travelers" ends when the trip ends pushed the design toward a "KR-JP lifestyle companion" — a much stickier product concept.

---

## 🧐 Final Project Retrospective

### 💡 Engineering for Real Travelers
SkyWear was built around a genuine pain point: travelers don't need more weather data — they need weather *intelligence*. By combining real-time API data with a proprietary outfit algorithm, bidirectional travel support, and lifecycle features, this project demonstrates that mobile engineering can deliver tangible, human-centered value.

### 🚀 Technical Evolution: Architecture as a Foundation
Starting from a simple API call and evolving into a fully layered MVVM application with Hilt DI, Room v3 migrations, DataStore persistence, and Firebase monitoring taught me that architecture is not overhead — it is the foundation that makes every feature addition faster and safer.

### 🌏 Bridging Markets Through Technology
As a developer targeting the Japanese IT market, SkyWear represents my approach to engineering: identify a real cross-cultural friction point, and solve it with clean, maintainable, and user-empathetic code.

---

## ✨ Contact

- **GitHub Repository**: https://github.com/2daKaizen-gun/skywear
- **Email**: hkys1223@gmail.com

---

<div align="center">
<sub>Built with ❤️ for KR-JP Travelers · SkyWear © 2026</sub>
</div>