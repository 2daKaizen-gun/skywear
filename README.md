# 📋 SkyWear: 🇰🇷🇯🇵 KR-JP Travel Coordinator

**"한·일 온도 비교 기반 스마트 트래블 코디 가이드"**

**SkyWear [Sky + Wear]** is a mobile solution designed to bridge the environmental gap between Korea and Japan. By comparing real-time weather data between departure and destination cities, it provides intuitive outfit recommendations to ensure a seamless travel experience.

---

## 🎯 Background & Motivation

### The Context: "Seamless Travel Experience"
Although Korea and Japan are neighboring countries, their perceived temperatures vary significantly due to differences in latitude and geography. Travelers often struggle with the uncertainty of whether their current attire is suitable for their destination (e.g., freezing in Seoul vs. mild in Osaka).

### The Problem
* **Tedious Comparison**: Users must manually toggle between different weather apps to compare temperatures.
* **Abstract Data**: Numerical temperatures (e.g., 8°C) don't intuitively suggest specific outfits like "heavy coat" or "light jacket."
* **Missing Local Context**: Lack of integrated information on humidity, wind chill, and Japan-specific travel essentials (e.g., 110V adapters).

### The Solution
* **Dual-City Dashboard**: Instant visualization of weather differences between KR and JP on a single screen.
* **Outfit Recommendation Engine**: An 8-stage algorithm that suggests optimal clothing based on temperature.
* **Travel-Specific Intelligence**: Integrated checklists for Japan travel and weather-based item reminders.

---

## 🛠 Tech Stack

| Category | Tech Stack |
| :--- | :--- |
| **Language** | **Kotlin** |
| **UI Framework** | **Jetpack Compose** |
| **Architecture** | **MVVM** (Model-View-ViewModel) |
| **Network** | **Retrofit2**, **OkHttp3** |
| **AI/Data** | **OpenWeatherMap API** |
| **Image Loading**| **Coil** |

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
  - [x] Phase 3-5: Design Asset Mapping Engine (Weather State to Visual Icons)

- **Phase 4**: Travel Intelligence & Data Persistence
  - [x] Phase 4-1: Implement Japan-Specific Travel Checklist using Room DB or DataStore
  - [x] Phase 4-2: Develop City Search & User Preference Management Features
  - [x] Phase 4-3: Build Background Notification Service for Daily Travel Briefing
  - [x] Phase 4-4: UI Polish & Interactive Elements (Lottie Animations & Dark Mode)

- **Phase 5**: Quality Assurance & Portfolio Finalization
  - [x] Phase 5-1: Execute UI Testing & Component Validation using Compose Test Rule
  - [x] Phase 5-2: Code Refactoring & Dependency Injection (Hilt) Optimization
  - [x] Phase 5-3: Final Build Generation (.APK)

- **Phase 6**: Main UI Implementation
  - [x] Phase 6-1: Navigation & Screen Integration
  - [] Phase 6-2: Dual-City Weather Dashboard Screen
  - [] Phase 6-3: City Search Screen
  - [] Phase 6-4: Japan Travel Checklist Screen

---

## ✨ Contact
* **GitHub**: https://github.com/2daKaizen-gun/skywear
* **Email**: hkys1223@gmail.com
