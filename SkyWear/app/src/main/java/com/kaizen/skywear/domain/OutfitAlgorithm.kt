package com.kaizen.skywear.domain

// 기온 기반 8단계 스마트 코디 추천 엔진
// 이후 체감온드/습도 보정 로직 추가 예정

// 8단계 코디 데이터 클래스
data class OutfitRecommendation(
    // 단계 (1~8)
    // 온도 범위 텍스트 (ex: "9~11°C")
    // 메인 코디 (ex: "롱패딩 + 목도리")
    // 서브 코디 (ex: "핫팩 챙기기")
    // 필수 아이템 (ex: ["장갑", "목도리"])
    // 온도 팔레트 컬러 (Color.kt 연동)
    // 대표 이모지
)

// 8단계 코디 알고리즘 메인 함수
fun getOutfitRecommendation(celsius: Double): OutfitRecommendation {

}

// 코디 단계 텍스트 요약(UI 카드 한 줄 표시)

// 필수 아이템 텍스트 (ex: "선크림 * 선글라스 * 믈")
