package com.kaizen.skywear.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
@TypeConverters(ChecklistConverters::class)
abstract class SkyWearDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: SkyWearDatabase?=null

        fun getDatabase(context: Context): SkyWearDatabase {
            // 한 번에 하나의 스레드만 접근 가능
            return INSTANCE?:synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkyWearDatabase::class.java,
                    "skywear_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                // 만든 인스턴스 저장해 다음 호출부터 재사용
                INSTANCE = instance
                instance
            }
        }

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
                    database.checklistDao().insertItems()
                }
            }
        }
    }
}

// 언어별 기본 체크리스트

// 한국어 체크리스트
private fun getKoreanChecklistItems(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "여권 (유효기간 6개월 이상)", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "항공권 예약 확인서", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "숙소 예약 확인서", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "여행자 보험 증서", category = ChecklistCategory.DOCUMENT),
    ChecklistItem(title = "엔화 환전", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "해외 결제 가능 카드", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "IC카드 (교통용)", category = ChecklistCategory.MONEY),
    ChecklistItem(title = "110V 어댑터 (일본 전압)", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "스마트폰 충전기", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "보조 배터리", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "포켓 Wi-Fi or 유심", category = ChecklistCategory.ELECTRONIC),
    ChecklistItem(title = "현지 날씨에 맞는 옷", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "편한 신발 (많이 걸음)", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "우산 or 우비", category = ChecklistCategory.CLOTHING),
    ChecklistItem(title = "상비약 (두통약, 소화제)", category = ChecklistCategory.HEALTH),
    ChecklistItem(title = "마스크", category = ChecklistCategory.HEALTH),
    ChecklistItem(title = "손 소독제", category = ChecklistCategory.HEALTH),
    ChecklistItem(title = "일본어 번역 앱 설치", category = ChecklistCategory.MISC),
    ChecklistItem(title = "구글맵 오프라인 저장", category = ChecklistCategory.MISC),
    ChecklistItem(title = "비상 연락처 메모", category = ChecklistCategory.MISC),
)

// 영어 체크리스트


// 일본어 체크리스트

