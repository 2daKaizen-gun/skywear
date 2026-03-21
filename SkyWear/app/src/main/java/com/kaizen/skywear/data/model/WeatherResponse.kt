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

)

data class Weather(

)

data class Wind(

)

data class Sys(

)

data class Coord(

)