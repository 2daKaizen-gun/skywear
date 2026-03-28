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


// WeatherVisual 확장 - 야간/주간 배경색 보정