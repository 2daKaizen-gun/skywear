package com.kaizen.skywear.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

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
fun ForecastItem.dateLabel(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateText) ?: return dateText
        SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        dateText.substring(5, 10) // fallback: "05-01"
    }
}

// 날짜만 추출 "2025-05-01"
fun ForecastItem.dateKey(): String = dateText.substring(0, 10)

// 시간만 추출
fun ForecastItem.timeLabel(): String = dateText.substring(11, 16)

// 날짜별 대표 슬롯 - 낮 12시 기준 (없으면 첫 번째로)
fun List<ForecastItem>.dailyRepresentative(): Map<String, ForecastItem> {
    return groupBy { it.dateKey() }
        .mapValues { (_, items) ->
            items.firstOrNull { it.timeLabel() == "12:00" }
                ?: items.firstOrNull { it.timeLabel() == "15:00" }
                ?: items.first()
        }
}

// DualForecastResult - KR + JP 예보 쌍


// DailyForecastPair - 날짜별 KR/JP 예보 쌍


