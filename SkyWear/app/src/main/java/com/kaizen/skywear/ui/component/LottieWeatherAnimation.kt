package com.kaizen.skywear.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaizen.skywear.R
import com.kaizen.skywear.domain.WeatherGroup

// 날씨 상태에 따라 Lottie 애니메이션 자동 선택 + 재생
// WeatherIconMapper의 WeatherGroup과 연동됨

@Composable
fun WeatherAnimation(
    weatherId: Int,
    modifier: Modifier = Modifier,
    isNight: Boolean = false
) {


}

// WeatherGroup -> Lottie Raw Resource 매핑
private fun getWeatherLottieRes(
    group: WeatherGroup,
    isNight: Boolean
): Int {
    return when (group) {
        WeatherGroup.CLEAR -> if (isNight) R.raw.weather_clear_night
            else R.raw.weather_sunny

        WeatherGroup.CLOUDS -> R.raw.weather_cloudy
    }
}