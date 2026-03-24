package com.kaizen.skywear.domain

import com.kaizen.skywear.ui.theme.TempChilly
import com.kaizen.skywear.ui.theme.TempCold
import com.kaizen.skywear.ui.theme.TempCool
import com.kaizen.skywear.ui.theme.TempFreezing
import com.kaizen.skywear.ui.theme.TempHot
import com.kaizen.skywear.ui.theme.TempMild
import com.kaizen.skywear.ui.theme.TempScorching
import com.kaizen.skywear.ui.theme.TempWarm

// 기온 기반 8단계 스마트 코디 추천 엔진
// 이후 체감온드/습도 보정 로직 추가 예정

// 8단계 코디 데이터 클래스
data class OutfitRecommendation(
    val stage: Int, // 단계 (1~8)
    val tempRange: String, // 온도 범위 텍스트 (ex: "9~11°C")
    val mainOutfit: String, // 메인 코디 (ex: "롱패딩 + 목도리")
    val subOutfit: String, // 서브 코디 (ex: "핫팩 챙기기")
    val essentialItems: List<String>, // 필수 아이템 (ex: ["장갑", "목도리"])
    val colorHex: androidx.compose.ui.graphics.Color, // 온도 팔레트 컬러 (Color.kt 연동)
    val emoji: String// 대표 이모지
)

// 8단계 코디 알고리즘 메인 함수
fun getOutfitRecommendation(celsius: Double): OutfitRecommendation {
    return when {

        // 1단계 - 폭염(28°C 이상)
        celsius >= 28 -> OutfitRecommendation(
            stage = 1,
            tempRange = "28°C 이상",
            mainOutfit = "반팔 + 반바지",
            subOutfit = "린넨 소재 추천",
            essentialItems = listOf("선크림", "선글라스", "물"),
            colorHex = TempScorching,
            emoji = "\uD83E\uDD75"
        )

        // 2단계 - 더움 (23~27°C)
        celsius >= 23 -> OutfitRecommendation(
            stage = 2,
            tempRange = "23~27°C",
            mainOutfit = "반팔 + 얇은 긴팔",
            subOutfit = "통풍 잘 되는 소재 추천",
            essentialItems = listOf("선크림", "얇은 겉옷"),
            colorHex = TempHot,
            emoji = "\uD83D\uDE0E"
        )

        // 3단계 - 따뜻함 (17~22°C)
        celsius >= 17 -> OutfitRecommendation(
            stage = 3,
            tempRange = "17~22°C",
            mainOutfit = "가디언 or 긴팔",
            subOutfit = "얇은 레이어링 추천",
            essentialItems = listOf("얇은 자켓"),
            colorHex = TempWarm,
            emoji = "\uD83C\uDF24\uFE0F"
        )

        // 4단계 - 선선함 (12~16°C)
        celsius >= 12 -> OutfitRecommendation(
            stage = 4,
            tempRange = "12~16°C",
            mainOutfit = "니트 + 재킷",
            subOutfit = "얇은 스카프도 OK",
            essentialItems = listOf("재킷", "얇은 스카프"),
            colorHex = TempMild,
            emoji = "\uD83C\uDF42"
        )

        // 5단계 - 쌀쌀함 (9~11°C)
        celsius >= 9 -> OutfitRecommendation(
            stage = 5,
            tempRange = "9~11°C",
            mainOutfit = "가벼운 코드 + 니트",
            subOutfit = "일본 여행 시 히트텍 추천",
            essentialItems = listOf("코트", "히트텍", "스카프"),
            colorHex = TempCool,
            emoji = "\uD83C\uDF41"
        )

        // 6단계 - 추움 (5~8°C)
        celsius >= 5 -> OutfitRecommendation(
            stage = 6,
            tempRange = "5~8°C",
            mainOutfit = "두꺼운 코트 + 레이어링",
            subOutfit = "히트텍 필수",
            essentialItems = listOf("두꺼운 코드", "히트텍", "장갑", "목도리"),
            colorHex = TempChilly,
            emoji = "\uD83E\uDDE5"
        )

        // 7단계 - 많이 추움 (0~4°C)
        celsius >= 0 -> OutfitRecommendation(
            stage = 7,
            tempRange = "0~4°C",
            mainOutfit = "롱패딩 + 목도리",
            subOutfit = "내복 착용 추천",
            essentialItems = listOf("롱패딩", "목도리", "장갑", "히트텍", "내복"),
            colorHex = TempCold,
            emoji = "\uD83E\uDD76"
        )

        // 8단계 - 혹한 (-1°C 이하)
        else -> OutfitRecommendation(
            stage = 8,
            tempRange = "-1°C 이하",
            mainOutfit = "롱패딩 + 목도리 + 핫팩",
            subOutfit = "외출 자제 권고",
            essentialItems = listOf("롱패딩", "목도리", "장갑", "핫팩", "히트텍", "내복"),
            colorHex = TempFreezing,
            emoji = "❄\uFE0F"
        )
    }
}

// 코디 단계 텍스트 요약(UI 카드 한 줄 표시)
fun OutfitRecommendation.toSummary(): String = "$emoji $mainOutfit"

// 필수 아이템 텍스트 (ex: "선크림 · 선글라스 · 믈")
fun OutfitRecommendation.essentialItemsLabel(): String =
    essentialItems.joinToString(" · ")