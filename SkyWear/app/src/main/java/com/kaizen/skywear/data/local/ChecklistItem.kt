package com.kaizen.skywear.data.local

// ChecklistItem
// Room DB Entity - 일본 여행 체크리스트 아이템


data class ChecklistItem(

)

// Checklist Category
enum class ChecklistCategory {
    DOCUMENT, // 서류 - 여권, 비자
    MONEY, // 금융 - 환전, 카드
    ELECTRONIC, // 전자기기- 어댑터, 충전기
    CLOTHING, // 의류 - 옷, 신발
    HEALTH, // 건강 - 상비약, 마스크
    MISC // 기타
}