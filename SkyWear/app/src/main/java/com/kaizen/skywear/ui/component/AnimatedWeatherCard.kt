package com.kaizen.skywear.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.model.weatherId
import com.kaizen.skywear.domain.WeatherVisual
import com.kaizen.skywear.domain.getOutfitRecommendation

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

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 국가 플래그 + 도시명
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$countryFlag ${weather.cityName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = visual.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lottie 날씨 애니메이션
                WeatherAnimation(
                    weatherId = weather.weatherId(),
                    isNight = visual.isNight,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 온도 (스케일 애니메이션)
                Text(
                    text = "${weather.tempRounded()}°",
                    style = MaterialTheme.typography.displayMedium,
                    color = countryColor,
                    modifier = Modifier.scale(scale)
                )

                // 코디 추천
                Text(
                    text = getOutfitRecommendation(weather.main.temp).mainOutfit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 습도 + 풍속


            }
        }
    }

}
