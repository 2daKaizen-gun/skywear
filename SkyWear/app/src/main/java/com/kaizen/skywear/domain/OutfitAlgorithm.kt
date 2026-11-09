package com.kaizen.skywear.domain

import androidx.annotation.StringRes
import com.kaizen.skywear.R

// 기온 기반 8단계 스마트 코디 추천 엔진
// 체감온도/습도 보정은 ContextAwareRecommendation 참고

// 8단계 코디 데이터 클래스
// 화면/알림 문구는 mainOutfitRes()의 현지화 리소스를 사용하고, mainOutfit은 기본(한국어) 설명용
data class OutfitRecommendation(
    val stage: Int, // 단계 (1~8)
    val mainOutfit: String, // 메인 코디 (ex: "롱패딩 + 목도리")
    val emoji: String // 대표 이모지
)

// 8단계 코디 알고리즘 메인 함수
fun getOutfitRecommendation(celsius: Double): OutfitRecommendation = when {
    celsius >= 28 -> OutfitRecommendation(1, "반팔 + 반바지", "🥵")             // 폭염 (28°C 이상)
    celsius >= 23 -> OutfitRecommendation(2, "반팔 + 얇은 긴팔", "😎")          // 더움 (23~27°C)
    celsius >= 17 -> OutfitRecommendation(3, "가디건 or 긴팔", "🌤️")      // 따뜻함 (17~22°C)
    celsius >= 12 -> OutfitRecommendation(4, "니트 + 재킷", "🍂")               // 선선함 (12~16°C)
    celsius >= 9  -> OutfitRecommendation(5, "가벼운 코트 + 니트", "🍁")         // 쌀쌀함 (9~11°C)
    celsius >= 5  -> OutfitRecommendation(6, "두꺼운 코트 + 레이어링", "🧥")     // 추움 (5~8°C)
    celsius >= 0  -> OutfitRecommendation(7, "롱패딩 + 목도리", "🥶")            // 많이 추움 (0~4°C)
    else          -> OutfitRecommendation(8, "롱패딩 + 목도리 + 핫팩", "❄️")         // 혹한 (-1°C 이하)
}

// 단계별 현지화된 메인 코디 문자열 리소스 (UI/알림 공통)
@StringRes
fun OutfitRecommendation.mainOutfitRes(): Int = when (stage) {
    1    -> R.string.outfit_stage1
    2    -> R.string.outfit_stage2
    3    -> R.string.outfit_stage3
    4    -> R.string.outfit_stage4
    5    -> R.string.outfit_stage5
    6    -> R.string.outfit_stage6
    7    -> R.string.outfit_stage7
    else -> R.string.outfit_stage8
}
