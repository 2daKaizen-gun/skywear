package com.kaizen.skywear.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.domain.WeatherVisual

// 날씨 카드 - Lottie 애니메이션 + 등장 애니메이션 포함
// Dual-City Dashboard 메인 카드 컴포넌트

@Composable
fun AnimatedWeatherCard(
    weather: WeatherResponse,
    visual: WeatherVisual,
    countryColor: androidx.compose.ui.graphics.Color,
    countryFlag: String,
    modifier: Modifier = Modifier
) {
    // 카드 등장 애니메이션

    // 온도 숫자 스케일 애니메이션

}
