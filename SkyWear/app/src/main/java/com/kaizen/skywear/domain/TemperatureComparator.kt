package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.model.temperatureGap
import kotlin.math.abs

// KR vs JP 온도 비교 분석 로직
// Dual-City Dashboard 핵심 비교 엔진

// 비교 분석 결과 데이터 클래스
data class TempComparisonResult(
    val krTemp: Int, // 서울 현재 온도
    val jpTemp: Int, // 오사카 현재 온도
    val gapLabel: String, // 온도 차이 텍스트 (ex: "+12°C")
    val gapDegree: Int, // 온도 차이 수치 (ex: 12)
    val comparisonMessage: String, // 비교 메시지 (ex: "오사카가 12도 더 따뜻해요!")
    val travelAdvice: String, // 여행 조언 (ex: "서울보다 가볍게 입으세요")
    val outfitGapLevel: OutfitGapLevel, // 체감 차이 단계
    val krOutfit: OutfitRecommendation, // 서울 코디 추천
    val jpOutfit: OutfitRecommendation // 오사카 코디 추천
)

// 온도 차이 단계(UI 강조 표시 활용)
enum class OutfitGapLevel {
    SIMILAR, // 차이 3°C 이하 — 비슷해요
    MODERATE, // 차이 4~9°C — 약간 달라요
    SIGNIFICANT // 차이 10°C 이상 — 많이 달라요!
}

// KR vs JP 온도 비교 분석 메인 함수
fun analyzeTempComparison(
    krWeather: WeatherResponse,
    jpWeather: WeatherResponse
): TempComparisonResult {

    val krTemp = krWeather.tempRounded()
    val jpTemp = jpWeather.tempRounded()
    val gap = temperatureGap(krWeather, jpWeather) // JP - KR
    val absGap = abs(gap)

    // 온도 차이 단계 계산
    val gapLevel = when {
        absGap <= 3 -> OutfitGapLevel.SIMILAR
        absGap <= 9 -> OutfitGapLevel.MODERATE
        else -> OutfitGapLevel.SIGNIFICANT
    }

    // 비교 메시지 생성
    val comparisonMessage = buildComparisonMessage(
        krTemp = krTemp,
        jpTemp = jpTemp,
        gap = gap,
        jpCityName = jpWeather.cityName
    )

    // 여행 조언 생성

}

// 비교 메시지 생성 로직
private fun buildComparisonMessage(
    krTemp: Int,
    jpTemp: Int,
    gap: Int,
    jpCityName: String
): String{
    return when {
        gap > 0 -> "${jpCityName}이(가) 서울보다 ${abs(gap)}°C 더 따뜻해요! \uD83C\uDF24\uFE0F"
        gap < 0 -> "${jpCityName}이(가) 서울보다 ${abs(gap)}°C 더 추워요! \uD83E\uDD76"
        else -> "서울과 ${jpCityName}의 기온이 똑같아요! \uD83C\uDF21\uFE0F"
    }
}

// 여행 조언 생성 로직
private fun buildTravelAdvice(
    gap: Int,
    gapLevel: OutfitGapLevel,
    krTemp: Int,
    jpTemp: Int
): String {
    return when (gapLevel) {

        OutfitGapLevel.SIMILAR -> when {
            jpTemp <= 0 -> "서울과 비슷하게 두껍게 입으세요. ❄\uFE0F"
            jpTemp <= 10 -> "서울과 비슷한 날씨에요. 코트를 챙기세요. \uD83E\uDDE5"
            jpTemp <= 20 -> "서울과 비슷한 날씨에요. 가볍게 입어도 돼요. \uD83C\uDF24\uFE0F"
            else -> "서울과 비슷하게 가볍게 입으세요. ☀\uFE0F"
        }

        OutfitGapLevel.MODERATE -> when {
            gap > 0 -> "서울보다 따뜻하니 한 겹 정도 가볍게 입으세요. \uD83C\uDF24\uFE0F"
            else -> "서울보다 추우니 한 겹 더 챙기세요. \uD83E\uDDE3"
        }

        OutfitGapLevel.SIGNIFICANT -> when {
            gap > 0 -> "서울보다 많이 따뜻해요! 서울 옷차림은 너무 두꺼울 수 있어요. \uD83C\uDF1E"
            else -> "서울보다 많이 추워요! 서울보다 훨씬 두껍게 입으세요. ❄\uFE0F"
        }
    }
}

// 코디 차이 요약 (ex: "롱패딩 → 가벼운 코트")


// 코디가 달라지는지 여부 (같은 단계면 false)
