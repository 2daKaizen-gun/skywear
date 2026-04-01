package com.kaizen.skywear.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Room DB 싱글톤 인스턴스 + 기본 체크리스트 데이터 초기화

@Database(
    entities = [ChecklistItem::class],
    version = 1,
    exportSchema = false
)
abstract class SkyWearDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: SkyWearDatabase?=null


    }

    // DB 최초 생성 시 기본 체크리스트 아이템 자동 삽입
    private class DatabaseCallback: Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // 싱글톤 DB 인스턴스가 null 아닐 때 실행
            INSTANCE?.let { database ->
                // DB 작업은 IO thread에서 비동기 실행
                CoroutineScope(Dispatchers.IO).launch {
                    // 기본 아이템 한번에 삽입
                    database.checklistDao().insertItems(getDefaultChecklistItems())
                }
            }
        }
    }
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

    // 전자
    ChecklistItem(title = "110V 어댑터 (일본 전압)", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "스마트폰 충전기", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "보조 배터리", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "이심 or 유심 or 도시락 와이파이", category = ChecklistCategory.ELECTRONIC),

    // 의류
    ChecklistItem(title = "현지 날씨에 맞는 옷", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "편한 신발 (도보 이동)", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "우산 or 우비", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "선글라스 등 기타 악세서리", category = ChecklistCategory.CLOTHING),

    // 건강
    ChecklistItem(title = "상비약 (두통약, 소화제 등)", category = ChecklistCategory.HEALTH),
    ChecklistItem(title = "마스크", category = ChecklistCategory.HEALTH),
    ChecklistItem(title = "손 소독제, 향수 등 기타 액체류", category = ChecklistCategory.HEALTH),

    // 기타
    ChecklistItem(title = "일본어 번역 앱", category = ChecklistCategory.MISC),
    ChecklistItem(title = "구글맵 오프라인 저장", category = ChecklistCategory.MISC),
    ChecklistItem(title = "비상 연락처 메모", category = ChecklistCategory.MISC),
    ChecklistItem(title = "여행 플래너 확인", category = ChecklistCategory.MISC),
    )