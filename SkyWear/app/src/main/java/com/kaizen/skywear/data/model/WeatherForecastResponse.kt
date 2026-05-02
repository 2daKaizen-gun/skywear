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

// 날짜별 대표 슬롯 + 시간 + 최저/최고 온도
fun List<ForecastItem>.dailyRepresentativeWithTime(): Map<String, Triple<ForecastItem, String, Pair<Int, Int>>> {
    return groupBy { it.dateKey() }
        .mapValues { (_, items) ->
            val representative =
                items.firstOrNull { it.timeLabel() == "12:00" }
                    ?: items.firstOrNull { it.timeLabel() == "15:00" }
                    ?: items.firstOrNull { it.timeLabel() == "09:00" }
                    ?: items.first()
            // 하루 전체 슬롯에서 최저/최고 계산
            val tempmin = items.minOf { it.main.temp }.toInt()
            val tempMax = items.maxOf { it.main.temp }.toInt()

            Triple(representative, representative.timeLabel(), Pair(tempmin, tempMax))
        }
}

// 기존 호환용
fun List<ForecastItem>.dailyRepresentative(): Map<String, ForecastItem> =
    dailyRepresentativeWithTime().mapValues { it.value.first }

// DualForecastResult - KR + JP 예보 쌍
data class DualForecastResult(
    val krForecast: Result<WeatherForecastResponse>,
    val jpForecast: Result<WeatherForecastResponse>
) {
    val isSuccess: Boolean get() = krForecast.isSuccess && jpForecast.isSuccess
    val errorMessage: String? get() = krForecast.exceptionOrNull()?.message
        ?: jpForecast.exceptionOrNull()?.message
}

// DailyForecastPair - 날짜별 KR/JP 예보 쌍
data class DailyForecastPair(
    val dateKey: String, // "2026-05-01"
    val dateLabel: String, // "05/01"
    val representativeTime: String, // "12:00" — 대표 슬롯 시간
    val krItem: ForecastItem,
    val jpItem: ForecastItem,
    val krTempMin: Int, // 하루 최저
    val krTempMax: Int, // 하루 최고
    val jpTempMin: Int,
    val jpTempMax: Int
)