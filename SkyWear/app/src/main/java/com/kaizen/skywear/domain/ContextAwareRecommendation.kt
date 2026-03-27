package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.WeatherResponse
import kotlin.math.pow

// 체감 온도, 습도 기반 코디 보정 로직
// OutfitAlgorithm 결과를 보정해서 더 정확한 추천 제공

// 보정된 최종 추천 결과
data class ContextAwareResult (
    val baseOutfit: OutfitRecommendation, // 기온 기반 기본 코디
    val adjustedOutfit: OutfitRecommendation, // 체감 온도 보정 후 코디
    val feelsLikeTemp: Double, // 계산된 체감 온도
    val humidityLevel: HumidityLevel, // 습도 단계
    val windLevel: WindLevel, // 온도 단계
    val contextMessage: String, // 보정 메시지 (ex: "바람이 강해서 더 춥게 느껴져요")
    val extraItems: List<String> // 추가 아이템 (ex: ["우산", "방풍 재킷"])
)

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
    val feelsLikeTemp = calculateFeelsLike(actualTemp, windSpeed, humidity)

    // 습도/바람 단계 분류

    // 기본 코디 (실제 기온 기반)

    // 보정 코디 (체감온도 기반)

    // 보정 메시지 생성

    // 추가 아이템 생성
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

// 보정 메시지 생성
private fun buildContextMessage(
    actualTemp: Double,
    feelsLikeTemp: Double,
    humidityLevel: HumidityLevel,
    windLevel: WindLevel
): String {
    val tempDiff = actualTemp - feelsLikeTemp

    return when {
        // 바람이 강해서 훨씬 춥게 느껴질 때
        windLevel == WindLevel.VERY_WINDY && tempDiff >= 5 ->
            "바람이 매우 강해 체감온도가 ${String.format("%.1f", tempDiff)}°C 낮아요! 방풍 재킷을 꼭 챙기세요. 🌬️"

        windLevel == WindLevel.WINDY && tempDiff >= 3 ->
            "바람이 강해서 실제보다 춥게 느껴져요. 한 겹 더 챙기세요. \uD83D\uDCA8"

        // 습도가 높아서 더 덥게 느껴질 때
        humidityLevel == HumidityLevel.VERY_HUMID && feelsLikeTemp > actualTemp ->
            "습도가 매우 높아서 체감온도가 더 높아요. 통풍이 잘 되는 옷을 입으세요. \uD83D\uDCA7"

        humidityLevel == HumidityLevel.HUMID ->
            "습도가 높은 편이에요. 습한 날씨에 주의하세요. \uD83C\uDF2B\uFE0F"

        // 건조할 때
        humidityLevel == HumidityLevel.DRY ->
            "건조한 날씨에요. 보습에 신경 쓰세요. \uD83C\uDFDC\uFE0F"

        // 쾌적할 때
        else -> "쾌적한 날씨예요. 기온에 맞게 입으면 돼요. \uD83D\uDE0A"
    }
}

// 날씨 상황별 추가 아이템 추천
private fun buildExtraItems(
    humidityLevel: HumidityLevel,
    windLevel: WindLevel,
    feelsLikeTemp: Double
): List<String> {
    val items = mutableListOf<String>()

    // 습도별 추가 아이템
    when (humidityLevel) {
        HumidityLevel.VERY_HUMID, HumidityLevel.HUMID -> {
            items.add("우산 (습한 날씨)")
            items.add("제습 기능 의류")
        }
        HumidityLevel.DRY -> {
            items.add("보습 크림")
            items.add("립밤")
        }
        else -> {}
    }

    // 바람별 추가 아이템
    when (windLevel) {
        WindLevel.VERY_WINDY -> {
            items.add("방풍 재킷")
            items.add("넥워머")
        }
        WindLevel.WINDY -> items.add("바람막이")
        else -> {}
    }

    // 체감온도별 추가 아이템
    when {
        feelsLikeTemp < 0 -> items.add("핫팩")
        feelsLikeTemp > 30 -> {
            items.add("손 선풍기")
            items.add("쿨링 타월")
        }
    }

    return items.distinct()
}

// 체감 온도 표시 텍스트 (ex: "체감 -5°C")


// 기온과 체감 온도 차이가 큰지 여부 (2°C 이상 차이나면 UI에서 강조)

