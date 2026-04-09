package com.kaizen.skywear.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // 온도 숫자 스케일 애니메이션
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "temp_scale"
    )



}
