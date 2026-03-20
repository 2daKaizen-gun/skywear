package com.kaizen.skywear.util

import com.kaizen.skywear.BuildConfig

object Constants {
    // API key - BuildConfig 통해 안전히 접근
    val WEATHER_API_KEY: String = BuildConfig.WEATHER_API_KEY

    // OpenWeatherMap Base URL
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // 기본 도시 설정
    const val DEFAULT_CITY_KR = "Seoul"
    const val DEFAULT_CITY_JP = "Osaka"

    // 날씨 단위 섭씨(metric)
    const val UNIT = "metric"

    // 언어 설정
    const val LANG_KR = "kr"
    const val LANG_JP = "ja"
}