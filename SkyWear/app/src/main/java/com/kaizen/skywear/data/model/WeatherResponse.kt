package com.kaizen.skywear.data.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

// OpenWeatherMap API 응답 데이터 클래스
data class WeatherResponse(
    @SerializedName("name") val cityName: String, // 도시명(ex: Seoul)
    @SerializedName("main") val main: MainWeather, // 온/습도 정보
    @SerializedName("weather") val weather: List<Weather>, //날씨 상태
    @SerializedName("wind") val wind: Wind, // 바람 정보
    @SerializedName("sys") val sys: Sys, // 국가 코드
    @SerializedName("coord") val coord: Coord, // 위/경도
    @SerializedName("dt") val timestamp: Long // 데이터 수집 시각(Unix)
)

data class MainWeather(
    @SerializedName("temp") val temp: Double, // 현재 온도(°C)
    @SerializedName("feels_like") val feelsLike: Double, // 체감 온도(°C)
    @SerializedName("temp_min") val tempMin: Double, // 최저 온도(°C)
    @SerializedName("temp_max") val tempMax: Double, // 최고 온도(°C)
    @SerializedName("humidity") val humidity: Int, // 습도(%)
    @SerializedName("pressure") val pressure: Int // 기압(hPa)
)

data class Weather(
    @SerializedName("id") val id: Int, // 날씨 코드
    @SerializedName("main") val main: String, // 날씨 그룹(ex: Clear, Rain)
    @SerializedName("description") val description: String, // 상세 설명 (ex: 맑음, 흐림)
    @SerializedName("icon") val icon: String // 아이콘 코드(ex: 01d)
)

data class Wind(
    @SerializedName("speed") val speed: Double, // 풍속(m/s)
    @SerializedName("deg") val deg: Int // 풍향(도)
)

data class Sys(
    @SerializedName("country") val country: String // 국가 코드(ex: KR, JP)
)

data class Coord(
    @SerializedName("lat") val lat: Double, // 위도
    @SerializedName("lon") val lon: Double // 경도
)