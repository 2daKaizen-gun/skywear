package com.kaizen.skywear.data.model

import kotlin.math.roundToInt

// DTO -> UI에서 쓰기 편한 형태로 변환하는 확장 함수

// 온도 소수점 반올림 (ex: 10.2 -> 10)
fun WeatherResponse.tempRounded(): Int = main.temp.roundToInt()

// 체감 온도 소수점 반올림
fun WeatherResponse.feelsLikeRounded(): Int = main.feelsLike.roundToInt()

// 날씨 상태 텍스트 (ex: "맑음" , "흐림")
fun WeatherResponse.weatherDescription(): String =
    weather.firstOrNull()?.description ?: "알 수 없음"

// 날씨 아이콘 코드 (ex: "01d")
fun WeatherResponse.iconCode(): String =
    weather.firstOrNull()?.icon ?: "01d"

// 날씨 ID
fun WeatherResponse.weatherId(): Int =
    weather.firstOrNull()?.id ?: 800

// KR <-> JP 온도 차이 계산 (ex: +12, -3)
fun temperatureGap(krWeather: WeatherResponse, jpWeather: WeatherResponse): Int =
    (jpWeather.main.temp - krWeather.main.temp).roundToInt()

// 온도 차이 표시 문자열 (ex: "+12°C", "-3°C")
fun temperatureGapLabel(krWeather: WeatherResponse, jpWeather: WeatherResponse): String {
    val gap = temperatureGap(krWeather, jpWeather)
    return if (gap>=0) "+${gap}°C" else "${gap}°C"
}

// 풍속 텍스트 (ex: "3.2 m/s")
fun WeatherResponse.windSpeedLabel(): String = "${wind.speed} m/s"

// 습도 텍스트 (ex: "습도 60%")
fun WeatherResponse.humidityLabel(): String = "습도 ${main.humidity}%"

// 국가 코드 -> 이모지 변환 (ex: "KR" -> "🇰🇷", "JP" → "🇯🇵")
fun WeatherResponse.flagEmoji(): String {
    return sys.country.uppercase().map {
        String(Character.toChars(it.code + 0x1F1A5))
    }.joinToString("")
}
