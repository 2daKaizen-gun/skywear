# 📋 SkyWear: 🇰🇷🇯🇵 KR-JP Travel Coordinator

**"한·일 온도 비교 기반 스마트 트래블 코디 가이드"**

**SkyWear [Sky + Wear]** is a mobile solution designed to bridge the environmental gap between Korea and Japan. By comparing real-time weather data between departure and destination cities, it provides intuitive outfit recommendations to ensure a seamless travel experience.

---

## 🎯 Background & Motivation

### The Context: "Seamless Travel Experience"
Although Korea and Japan are neighboring countries, their perceived temperatures vary significantly due to differences in latitude and geography. Travelers often struggle with the uncertainty of whether their current attire is suitable for their destination (e.g., freezing in Seoul vs. mild in Tokyo).

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

## ✅ Milestones

### Phase 1: Project Foundation & UI Prototyping
* [] **Phase 1-1**: Initialize GitHub Repository & Technical Documentation (README.md)
* [ ] **Phase 1-2**: Android Studio Project Initialization (Empty Compose Activity)
* [ ] **Phase 1-3**: Design System Definition (Color Palette, Typography, & Brand Identity)
* [ ] **Phase 1-4**: Prototyping Core UI Layout (Dual-City Comparison Dashboard)

### Phase 2: Network Layer & Weather API Integration
* [ ] **Phase 2-1**: Configure API Environments & Secure Key Management (local.properties)
* [ ] **Phase 2-2**: Define Retrofit Interfaces & Weather Data Transfer Objects (DTO)
* [ ] **Phase 2-3**: Implement Weather Fetching Logic (Source: KR / Destination: JP)
* [ ] **Phase 2-4**: Architect Error Handling Strategy & UI Loading State Management

### Phase 3: Logic Development & Outfit Recommendation Engine
* [ ] **Phase 3-1**: Implement Temperature-based 8-Stage Outfit Recommendation Algorithm
* [ ] **Phase 3-2**: Develop KR-JP Temperature Comparison & Contextual Messaging Logic
* [ ] **Phase 3-3**: Weather Icon Mapping & Visual Data Processing
* [ ] **Phase 3-4**: Reactive State Management Integration via ViewModel & StateFlow

### Phase 4: Advanced Travel Features & UX Enhancement
* [ ] **Phase 4-1**: Implementation of Japan-Specific Travel Checklist (Local Persistence)
* [ ] **Phase 4-2**: City Search & Selection Functionality (Google Places API)
* [ ] **Phase 4-3**: Design & Implementation of Temperature-based Local Notifications
* [ ] **Phase 4-4**: UI Polish: Dark Mode Support & Lottie Animations

### Phase 5: Quality Assurance & Finalization
* [ ] **Phase 5-1**: Comprehensive UI/UX Testing (Emulator & Physical Device)
* [ ] **Phase 5-2**: Code Refactoring & Dependency Injection (Hilt) Optimization
* [ ] **Phase 5-3**: Final Technical Documentation & Portfolio Structuring
* [ ] **Phase 5-4**: Production Build (APK) Generation & Project Retrospective

---

## ✨ Contact
* **GitHub**: [https://github.com/2daKaizen-gun](https://github.com/2daKaizen-gun)
* **Email**: hkys1223@gmail.com
