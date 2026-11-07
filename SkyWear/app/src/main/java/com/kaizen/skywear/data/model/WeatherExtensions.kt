package com.kaizen.skywear.data.model

import kotlin.math.roundToInt

// DTO -> UI에서 쓰기 편한 형태로 변환하는 확장 함수

// 온도 소수점 반올림 (ex: 10.2 -> 10)
fun WeatherResponse.tempRounded(): Int = main.temp.roundToInt()

// 날씨 아이콘 코드 (ex: "01d")
fun WeatherResponse.iconCode(): String =
    weather.firstOrNull()?.icon ?: "01d"

// 날씨 ID
fun WeatherResponse.weatherId(): Int =
    weather.firstOrNull()?.id ?: 800

// KR <-> JP 온도 차이 계산 (ex: +12, -3)
fun temperatureGap(krWeather: WeatherResponse, jpWeather: WeatherResponse): Int =
    (jpWeather.main.temp - krWeather.main.temp).roundToInt()
