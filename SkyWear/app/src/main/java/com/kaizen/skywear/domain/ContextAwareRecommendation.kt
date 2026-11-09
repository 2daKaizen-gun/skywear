package com.kaizen.skywear.domain

import androidx.annotation.StringRes
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.WeatherResponse
import kotlin.math.pow

data class ContextAwareResult (
    val baseOutfit: OutfitRecommendation, // 기온 기반 기본 코디
    val adjustedOutfit: OutfitRecommendation, // 체감 온도 보정 후 코디
    val feelsLikeTemp: Double, // 계산된 체감 온도
    val humidityLevel: HumidityLevel, // 습도 단계
    val windLevel: WindLevel, // 바람 단계
    val extraItems: List<ExtraItem> // 추가로 챙길 아이템 (ex: 우산, 방풍 재킷)
)

// 날씨 상황별 추가 아이템 (화면 표시 문구는 labelRes 현지화 리소스)
enum class ExtraItem(@StringRes val labelRes: Int) {
    UMBRELLA(R.string.extra_umbrella),
    MOISTURE_WICKING_CLOTHES(R.string.extra_moisture_wicking),
    MOISTURIZER(R.string.extra_moisturizer),
    LIP_BALM(R.string.extra_lip_balm),
    WINDPROOF_JACKET(R.string.extra_windproof_jacket),
    NECK_WARMER(R.string.extra_neck_warmer),
    WINDBREAKER(R.string.extra_windbreaker),
    HAND_WARMER(R.string.extra_hand_warmer),
    HANDHELD_FAN(R.string.extra_handheld_fan),
    COOLING_TOWEL(R.string.extra_cooling_towel)
}

// 습도 단계
enum class HumidityLevel {
    DRY, // 40% 미만 - 건조
    COMFORTABLE, // 40~60% - 쾌적
    HUMID, // 61~80% - 습함
    VERY_HUMID // 81% 초과 - 매우 습함
}

// 바람 단계
enum class WindLevel {
    CALM, // 3 m/s 미만 - 잔잔함
    BREEZY, // 3 m/s ~ 6 m/s - 약간 바람
    WINDY, // 6 m/s ~ 10 m/s - 강한 바람
    VERY_WINDY // 10 m/s 초과 - 매우 강한 바람
}

// 체감 온도, 습도 보정 메인 함수
fun buildContextAwareRecommendation (weather: WeatherResponse): ContextAwareResult {

    val actualTemp = weather.main.temp
    val humidity = weather.main.humidity
    val windSpeed = weather.wind.speed

    // 체감온도 계산
    val feelsLike = calculateFeelsLike(actualTemp, windSpeed, humidity)

    return ContextAwareResult(
        baseOutfit     = getOutfitRecommendation(actualTemp),
        adjustedOutfit = getOutfitRecommendation(feelsLike),
        feelsLikeTemp  = feelsLike,
        humidityLevel  = classifyHumidity(humidity),
        windLevel      = classifyWind(windSpeed),
        extraItems     = buildExtraItems(
            humidityLevel = classifyHumidity(humidity),
            windLevel     = classifyWind(windSpeed),
            feelsLikeTemp = feelsLike,
            isRainy       = isRainyWeather(weather.weather.firstOrNull()?.id)
        )
    )
}

// 체감 온도 계산 (Wind Chill + Heat Index 통합)
// 10°이하: Wind Chill 공식 적용 (바람에 의한 체감 저하)
// 27°이상: Head Index 공식 적용 (습도에 의한 체감 상승)
// 그 외: 실제 기온을 그대로 적용
private fun calculateFeelsLike (
    temp: Double,
    windSpeed: Double, // m/s
    humidity: Int
): Double {
    return when {
        // Wind Chill - 추울 때 바람이 체감온도 낮춤
        temp <= 10.0 -> {
            val windKmh = windSpeed * 3.6 // m/s를 km/h로 변환
            if (windKmh >= 4.8) {
                // Canadian Wind Chill 공식
                // 체감 온도 = 13.12 + 0.6215*실제 기온 - 11.37*풍속.pow(0.16) + 0.3965*기온*풍속.pow(0.16)
                13.12 + 0.6215 * temp -
                        11.37 * windKmh.pow(0.16) +
                        0.3965 * temp * windKmh.pow(0.16)
            } else {
                temp // 바람 약하면 실제 기온과 동일
            }
        }

        // Heat Index - 더울 때 습도가 체감 온도 높임
        temp >= 27.0 -> {
            // Steadman Heat Index 간략 공식
            val f = temp * 1.8 + 32 // 섭씨 -> 화씨 변환
            // HI = 0.5 * {T + 61.0 + [(T-68.0) * 1.2] + (RH * 0.094)}
            val hi =  0.5 * (f + 61.0 + ((f - 68.0) * 1.2) + (humidity * 0.094))

            (hi - 32) / 1.8 // 화씨 -> 섭씨 변환
        }
        // 그 외 - 실제 기온 사용
        else -> temp
    }
}

// 습도 단계 분류
private fun classifyHumidity(humidity: Int): HumidityLevel = when {
    humidity < 40 -> HumidityLevel.DRY
    humidity <= 60 -> HumidityLevel.COMFORTABLE
    humidity <= 80 -> HumidityLevel.HUMID
    else -> HumidityLevel.VERY_HUMID
}

// 바람 단계 분류
private fun classifyWind(windSpeed: Double): WindLevel = when {
    windSpeed < 3.0 -> WindLevel.CALM
    windSpeed < 6.0 -> WindLevel.BREEZY
    windSpeed < 10.0 -> WindLevel.WINDY
    else -> WindLevel.VERY_WINDY
}

// 날씨 상황별 추가 아이템 추천
private fun buildExtraItems(
    humidityLevel: HumidityLevel,
    windLevel: WindLevel,
    feelsLikeTemp: Double,
    isRainy: Boolean
): List<ExtraItem> {
    val items = mutableListOf<ExtraItem>()

    // 비/이슬비/뇌우 예보 시 우산
    if (isRainy) items.add(ExtraItem.UMBRELLA)

    // 습도별 추가 아이템
    when (humidityLevel) {
        HumidityLevel.VERY_HUMID, HumidityLevel.HUMID -> items.add(ExtraItem.MOISTURE_WICKING_CLOTHES)
        HumidityLevel.DRY -> {
            items.add(ExtraItem.MOISTURIZER)
            items.add(ExtraItem.LIP_BALM)
        }
        else -> {}
    }

    // 바람별 추가 아이템
    when (windLevel) {
        WindLevel.VERY_WINDY -> {
            items.add(ExtraItem.WINDPROOF_JACKET)
            items.add(ExtraItem.NECK_WARMER)
        }
        WindLevel.WINDY -> items.add(ExtraItem.WINDBREAKER)
        else -> {}
    }

    // 체감온도별 추가 아이템
    when {
        feelsLikeTemp < 0 -> items.add(ExtraItem.HAND_WARMER)
        feelsLikeTemp > 30 -> {
            items.add(ExtraItem.HANDHELD_FAN)
            items.add(ExtraItem.COOLING_TOWEL)
        }
    }

    return items.distinct()
}

// 비 계열 날씨 코드 (2xx 뇌우, 3xx 이슬비, 5xx 비)
// https://openweathermap.org/weather-conditions
private fun isRainyWeather(weatherId: Int?): Boolean =
    weatherId != null && (weatherId in 200..321 || weatherId in 500..531)
