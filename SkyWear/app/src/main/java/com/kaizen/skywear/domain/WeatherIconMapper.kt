package com.kaizen.skywear.domain

import androidx.compose.ui.graphics.Color
import com.kaizen.skywear.ui.theme.TempChilly
import com.kaizen.skywear.ui.theme.TempCold
import com.kaizen.skywear.ui.theme.TempCool
import com.kaizen.skywear.ui.theme.TempFreezing
import com.kaizen.skywear.ui.theme.TempHot
import com.kaizen.skywear.ui.theme.TempMild
import com.kaizen.skywear.ui.theme.TempScorching
import com.kaizen.skywear.ui.theme.TempWarm

// WeatherIconMapper
// OpenWeatherMap 날씨 코드 -> 이모지, 색상, 배경 매핑 엔진
// 3-1(OutfitAlgorithm), 3-3(ContextAware)과 연동

// 날씨 아이콘 매핑 결과
data class WeatherVisual(
    val emoji: String,              // 날씨 이모지 (ex: "☀️")
    val label: String,              // 날씨 텍스트 (ex: "맑음")
    val backgroundColor: Color,     // 카드 배경색 (온도 팔레트 연동)
    val isNight: Boolean            // 야간 여부 (아이콘 코드 "n" 포함 시)
)

// OpenWeatherMap 날씨 코드 -> WeatherVisual 매핑
// 날씨 코드 참고: https://openweathermap.org/weather-conditions
fun mapWeatherCodeToVisual(
    weatherId: Int,
    iconCode: String, // ex: "01d", "01n"..
    temp: Double // 온도 기반 배경색 결정
): WeatherVisual {

    val isNight = iconCode.endsWith("n")
    val bgColor = getTempBackgroundColor(temp)

    return when (weatherId) {
        // Thunderstorm(200번대)

        // Drizzle (300번대)

        // Rain (500번대)

        // Snow (600번대)

        // Atmosphere (700번대)

        // Clear (800)

        // Clouds (801~804)

        // Unknown
        else -> WeatherVisual("\uD83C\uDF21\uFE0F", "알 수 없음", bgColor, isNight)
    }
}

// 온도 기반 배경색 (Color.kt 팔레트 연동)
private fun getTempBackgroundColor(celsius: Double): Color = when {
    celsius >= 28 -> TempScorching
    celsius >= 23 -> TempHot
    celsius >= 17 -> TempWarm
    celsius >= 12 -> TempMild
    celsius >= 9 -> TempCool
    celsius >= 5 -> TempChilly
    celsius >= 0 -> TempCold
    else -> TempFreezing
}

// 날씨 코드 그룹 분류 (배경 gradient 등 UI 처리에 활용)
enum class WeatherGroup {
    CLEAR, // 맑음
    CLOUDS, // 구름
    RAIN, // 비
    SNOW, // 눈
    THUNDERSTORM, // 뇌우
    DRIZZLE, // 이슬비
    ATMOSPHERE, // 안개/먼지
    UNKNOWN
}

fun classifyWeatherGroup(weatherId: Int): WeatherGroup = when (weatherId) {
    800 -> WeatherGroup.CLEAR
    in 801..804 -> WeatherGroup.CLOUDS
    in 500..531 -> WeatherGroup.RAIN
    in 600..622 -> WeatherGroup.SNOW
    in 200..232 -> WeatherGroup.THUNDERSTORM
    in 300..321 -> WeatherGroup.DRIZZLE
    in 700..781 -> WeatherGroup.ATMOSPHERE
    else -> WeatherGroup.UNKNOWN
}

// WeatherVisual 확장 - 야간/주간 배경색 보정