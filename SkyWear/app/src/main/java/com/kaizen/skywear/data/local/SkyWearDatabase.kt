package com.kaizen.skywear.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Room DB 싱글톤 인스턴스 + 기본 체크리스트 데이터 초기화

@Database(
    entities = [ChecklistItem::class],
    version = 1,
    exportSchema = false
)
abstract class SkyWearDatabase : RoomDatabase() {

}

// 일본 여행 기본 체크리스트 아이템
fun getDefaultChecklistItems(): List<ChecklistItem> = listOf(

    // 서류 구비 및 확인
    ChecklistItem(title = "여권 (유효기간 6개월 이상)", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "항공권 예약 확인", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "숙소 예약 확인", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "여행자 보험 확인", category = ChecklistCategory.DOCUMENT),

    // 금융
    ChecklistItem(title = "엔화 환전", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "해외 결제 가능 카드", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "교통 카드", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "트레블 카드", category = ChecklistCategory.MONEY),


    )