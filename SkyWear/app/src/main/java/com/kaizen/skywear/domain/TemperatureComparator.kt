package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.model.temperatureGap
import com.kaizen.skywear.data.model.temperatureGapLabel
import kotlin.math.abs

// KR vs JP 온도 비교 분석 로직
// Dual-City Dashboard 핵심 비교 엔진

// 비교 분석 결과 데이터 클래스
data class TempComparisonResult(
    val krTemp: Int, // 서울 현재 온도
    val jpTemp: Int, // 오사카 현재 온도
    val jpCityName: String, // UI에서 메시지 생성 시 사용
    val gapLabel: String, // 온도 차이 텍스트 (ex: "+12°C")
    val gapDegree: Int, // 온도 차이 수치 (ex: 12)
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

    return TempComparisonResult(
        krTemp         = krTemp,
        jpTemp         = jpTemp,
        jpCityName     = jpWeather.cityName,
        gapLabel       = temperatureGapLabel(krWeather, jpWeather),
        gapDegree      = gap,
        outfitGapLevel = gapLevel,
        krOutfit       = getOutfitRecommendation(krWeather.main.temp),
        jpOutfit       = getOutfitRecommendation(jpWeather.main.temp)
    )
}

// 코디 차이 요약
fun TempComparisonResult.isOutfitDifferent(): Boolean =
    krOutfit.stage != jpOutfit.stage