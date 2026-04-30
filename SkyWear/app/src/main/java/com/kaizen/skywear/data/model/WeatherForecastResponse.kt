package com.kaizen.skywear.data.model

import com.google.gson.annotations.SerializedName

// OpenWeatherMap / forecast API DTO
// 5일 / 3시간 간격 예보

data class WeatherForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: ForecastCity
)

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("main") val main: MainWeather,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dateText: String // "2026-05-01 12:00:00"
)

data class ForecastCity(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String
)

// ForecastItem Extension Functions

// 날짜 문자열 -> "05/01" 형식


// 날짜만 추출 "2025-05-01"


// 시간만 추출


// 날짜별 대표 슬롯 - 낮 12시 기준 (없으면 첫 번째로)


// DualForecastResult - KR + JP 예보 쌍


// DailyForecastPair - 날짜별 KR/JP 예보 쌍


