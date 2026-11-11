package com.kaizen.skywear.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// OpenWeatherMap / forecast API DTO
// 5일 / 3시간 간격 예보

data class WeatherForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: ForecastCity
)

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long, // 예보 시각 (Unix, UTC)
    @SerializedName("main") val main: MainWeather,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dateText: String // "2026-05-01 12:00:00" (UTC 기준)
)

data class ForecastCity(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("timezone") val utcOffsetSeconds: Int = 0 // 도시의 UTC 오프셋(초), KR/JP = 32400
)

// ForecastItem Extension Functions
// dt_txt는 UTC 기준이라 그대로 쓰면 KR/JP(UTC+9)에서 날짜·시간이 9시간 어긋남
// -> dt + 도시 UTC 오프셋으로 현지 시각을 계산해 사용

private val DATE_LABEL_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd")
private val TIME_LABEL_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

// 현지 시각
fun ForecastItem.localDateTime(utcOffsetSeconds: Int): LocalDateTime =
    LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofTotalSeconds(utcOffsetSeconds))

// 현지 날짜 "2026-05-01"
fun ForecastItem.dateKey(utcOffsetSeconds: Int): String =
    localDateTime(utcOffsetSeconds).toLocalDate().toString()

// 현지 날짜 "05/01"
fun ForecastItem.dateLabel(utcOffsetSeconds: Int): String =
    localDateTime(utcOffsetSeconds).format(DATE_LABEL_FORMAT)

// 현지 시간 "12:00"
fun ForecastItem.timeLabel(utcOffsetSeconds: Int): String =
    localDateTime(utcOffsetSeconds).format(TIME_LABEL_FORMAT)

// 날짜별 대표 슬롯 + 시간 + 최저/최고 온도 (모두 현지 시각 기준)
fun List<ForecastItem>.dailyRepresentativeWithTime(
    utcOffsetSeconds: Int
): Map<String, Triple<ForecastItem, String, Pair<Int, Int>>> {
    return groupBy { it.dateKey(utcOffsetSeconds) }
        .mapValues { (_, items) ->
            val representative =
                items.firstOrNull { it.timeLabel(utcOffsetSeconds) == "12:00" }
                    ?: items.firstOrNull { it.timeLabel(utcOffsetSeconds) == "15:00" }
                    ?: items.firstOrNull { it.timeLabel(utcOffsetSeconds) == "09:00" }
                    ?: items.first()
            // 하루 전체 슬롯에서 최저/최고 계산 (현재 기온 표시와 동일하게 반올림)
            val tempMin = items.minOf { it.main.temp }.roundToInt()
            val tempMax = items.maxOf { it.main.temp }.roundToInt()

            Triple(representative, representative.timeLabel(utcOffsetSeconds), Pair(tempMin, tempMax))
        }
}

// DualForecastResult - KR + JP 예보 쌍
data class DualForecastResult(
    val krForecast: Result<WeatherForecastResponse>,
    val jpForecast: Result<WeatherForecastResponse>
) {
    val isSuccess: Boolean get() = krForecast.isSuccess && jpForecast.isSuccess
    val error: Throwable? get() = krForecast.exceptionOrNull() ?: jpForecast.exceptionOrNull()
}

// DailyForecastPair - 날짜별 KR/JP 예보 쌍
data class DailyForecastPair(
    val dateKey: String, // "2026-05-01"
    val dateLabel: String, // "05/01"
    val representativeTime: String, // "12:00" — 대표 슬롯 시간 (현지 시각)
    val krItem: ForecastItem,
    val jpItem: ForecastItem,
    val krTempMin: Int, // 하루 최저
    val krTempMax: Int, // 하루 최고
    val jpTempMin: Int,
    val jpTempMax: Int
)
