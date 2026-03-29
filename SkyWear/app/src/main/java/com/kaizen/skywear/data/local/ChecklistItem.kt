package com.kaizen.skywear.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// ChecklistItem
// Room DB Entity - 일본 여행 체크리스트 아이템

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String, // 아이템 이름 (ex: "여권")
    val category: ChecklistCategory, // 카테고리 (ex: DOCUMENT)
    val isChecked: Boolean = false, // 체크 여부
    val isDefault: Boolean = true, // 기본 제공 아이템 여부
    val memo: String = "" // 메모(선택적)
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