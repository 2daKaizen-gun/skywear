package com.kaizen.skywear.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

// v1 → v2: destination 컬럼 추가 (ChecklistItem)
// v2 → v3: subscribed_cities, travel_journals 테이블 추가
@Database(
    entities = [
        ChecklistItem::class,
        SubscribedCity::class,
        TravelJournal::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(ChecklistConverters::class)
abstract class SkyWearDatabase : RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao
    abstract fun subscribedCityDao(): SubscribedCityDao
    abstract fun travelJournalDao(): TravelJournalDao

    companion object {
        @Volatile private var INSTANCE: SkyWearDatabase? = null

        // v1 → v2: destination 컬럼 추가
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE checklist_items ADD COLUMN destination TEXT NOT NULL DEFAULT 'JP'")
            }
        }

        // v2 → v3: subscribed_cities, travel_journals 테이블 추가
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS subscribed_cities (
                        nameEn TEXT NOT NULL PRIMARY KEY,
                        nameKo TEXT NOT NULL,
                        nameJa TEXT NOT NULL,
                        country TEXT NOT NULL,
                        emoji TEXT NOT NULL,
                        isAlertOn INTEGER NOT NULL DEFAULT 1,
                        lastTemp REAL NOT NULL DEFAULT 0.0,
                        lastWeatherDesc TEXT NOT NULL DEFAULT '',
                        addedAt INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS travel_journals (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        destination TEXT NOT NULL,
                        destinationEmoji TEXT NOT NULL,
                        startDate TEXT NOT NULL,
                        endDate TEXT NOT NULL,
                        departTemp INTEGER NOT NULL,
                        departWeatherDesc TEXT NOT NULL,
                        departOutfit TEXT NOT NULL,
                        returnTemp INTEGER NOT NULL,
                        returnWeatherDesc TEXT NOT NULL,
                        returnOutfit TEXT NOT NULL,
                        memo TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): SkyWearDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkyWearDatabase::class.java,
                    "skywear_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val lang = Locale.getDefault().language
                    database.checklistDao().insertItems(getJapanTravelItems(lang))
                    database.checklistDao().insertItems(getKoreaTravelItems(lang))
                }
            }
        }
    }
}

// 여행 방향별 아이템 — 언어별 분기
fun getJapanTravelItems(lang: String = Locale.getDefault().language): List<ChecklistItem> =
    when (lang) {
        "ja" -> getJapanTravelChecklistJa()
        "en" -> getJapanTravelChecklistEn()
        else -> getJapanTravelChecklistKo()
    }

fun getKoreaTravelItems(lang: String = Locale.getDefault().language): List<ChecklistItem> =
    when (lang) {
        "ja" -> getKoreaTravelChecklistJa()
        "en" -> getKoreaTravelChecklistEn()
        else -> getKoreaTravelChecklistKo()
    }

// 일본 여행 - 한국어
fun getJapanTravelChecklistKo(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "여권 (유효기간 6개월 이상)",  category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "항공권 예약 확인서",           category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "숙소 예약 확인서",             category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "여행자 보험 증서",             category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "엔화 환전",                   category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "해외 결제 가능 카드",          category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "IC카드 (교통용)",              category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "110V 어댑터 (일본 전압)",      category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "스마트폰 충전기",              category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "보조 배터리",                  category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "포켓 Wi-Fi or 유심",          category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "현지 날씨에 맞는 옷",          category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "편한 신발 (많이 걸음)",         category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "우산 or 우비",                 category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "상비약 (두통약, 소화제)",       category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "마스크",                      category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "손 소독제",                   category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "일본어 번역 앱 설치",          category = ChecklistCategory.MISC,     destination = "JP"),
    ChecklistItem(title = "구글맵 오프라인 저장",          category = ChecklistCategory.MISC,     destination = "JP"),
    ChecklistItem(title = "비상 연락처 메모",             category = ChecklistCategory.MISC,     destination = "JP"),
)

// 일본 여행 - 영어
fun getJapanTravelChecklistEn(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "Passport (valid 6+ months)",    category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "Flight booking confirmation",   category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "Hotel booking confirmation",    category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "Travel insurance certificate",  category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "Exchange Japanese Yen",         category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "International credit card",     category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "IC Card (for transit)",         category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "110V power adapter (Japan)",    category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "Smartphone charger",            category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "Portable battery bank",         category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "Pocket Wi-Fi or SIM card",     category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "Weather-appropriate clothing",  category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "Comfortable walking shoes",     category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "Umbrella or raincoat",          category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "Basic medicine",                category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "Face mask",                    category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "Hand sanitizer",               category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "Install Japanese translation app", category = ChecklistCategory.MISC,  destination = "JP"),
    ChecklistItem(title = "Save offline Google Maps",     category = ChecklistCategory.MISC,     destination = "JP"),
    ChecklistItem(title = "Note emergency contacts",      category = ChecklistCategory.MISC,     destination = "JP"),
)

// 일본 여행 - 일본어
fun getJapanTravelChecklistJa(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "パスポート（有効期限6ヶ月以上）", category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "航空券予約確認書",              category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "宿泊予約確認書",               category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "海外旅行保険証書",              category = ChecklistCategory.DOCUMENT, destination = "JP"),
    ChecklistItem(title = "円両替",                      category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "海外決済可能なカード",           category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "ICカード（交通用）",            category = ChecklistCategory.MONEY,    destination = "JP"),
    ChecklistItem(title = "110V変圧器（日本の電圧）",      category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "スマートフォン充電器",           category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "モバイルバッテリー",            category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "ポケットWi-Fi または SIM",     category = ChecklistCategory.ELECTRONIC, destination = "JP"),
    ChecklistItem(title = "現地の天気に合った服",           category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "歩きやすい靴",                 category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "傘またはレインコート",           category = ChecklistCategory.CLOTHING, destination = "JP"),
    ChecklistItem(title = "常備薬（頭痛薬、胃薬）",        category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "マスク",                      category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "手指消毒剤",                  category = ChecklistCategory.HEALTH,   destination = "JP"),
    ChecklistItem(title = "日本語翻訳アプリのインストール", category = ChecklistCategory.MISC,     destination = "JP"),
    ChecklistItem(title = "Googleマップのオフライン保存",  category = ChecklistCategory.MISC,     destination = "JP"),
    ChecklistItem(title = "緊急連絡先のメモ",             category = ChecklistCategory.MISC,     destination = "JP"),
)

// 한국 여행 - 한국어
fun getKoreaTravelChecklistKo(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "여권 (유효기간 6개월 이상)",   category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "항공권 예약 확인서",            category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "숙소 예약 확인서",              category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "여행자 보험 증서",              category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "원화 환전",                    category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "해외 결제 가능 카드",           category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "T-money 카드 (교통용)",        category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "220V 어댑터 (한국 전압)",      category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "스마트폰 충전기",               category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "보조 배터리",                   category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "포켓 Wi-Fi or 유심",           category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "현지 날씨에 맞는 옷",           category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "편한 신발 (많이 걸음)",          category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "우산 or 우비",                  category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "상비약 (두통약, 소화제)",        category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "마스크",                       category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "손 소독제",                    category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "한국어 번역 앱 설치",           category = ChecklistCategory.MISC,     destination = "KR"),
    ChecklistItem(title = "구글맵 오프라인 저장",           category = ChecklistCategory.MISC,     destination = "KR"),
    ChecklistItem(title = "비상 연락처 메모",              category = ChecklistCategory.MISC,     destination = "KR"),
)

// 한국 여행 - 영어
fun getKoreaTravelChecklistEn(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "Passport (valid 6+ months)",    category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "Flight booking confirmation",   category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "Hotel booking confirmation",    category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "Travel insurance certificate",  category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "Exchange Korean Won",           category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "International credit card",     category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "T-money card (for transit)",   category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "220V power adapter (Korea)",   category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "Smartphone charger",            category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "Portable battery bank",         category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "Pocket Wi-Fi or SIM card",    category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "Weather-appropriate clothing", category = ChecklistCategory.CLOTHING,  destination = "KR"),
    ChecklistItem(title = "Comfortable walking shoes",    category = ChecklistCategory.CLOTHING,  destination = "KR"),
    ChecklistItem(title = "Umbrella or raincoat",         category = ChecklistCategory.CLOTHING,  destination = "KR"),
    ChecklistItem(title = "Basic medicine",               category = ChecklistCategory.HEALTH,    destination = "KR"),
    ChecklistItem(title = "Face mask",                   category = ChecklistCategory.HEALTH,    destination = "KR"),
    ChecklistItem(title = "Hand sanitizer",              category = ChecklistCategory.HEALTH,    destination = "KR"),
    ChecklistItem(title = "Install Korean translation app", category = ChecklistCategory.MISC,   destination = "KR"),
    ChecklistItem(title = "Save offline Google Maps",    category = ChecklistCategory.MISC,      destination = "KR"),
    ChecklistItem(title = "Note emergency contacts",     category = ChecklistCategory.MISC,      destination = "KR"),
)

// 한국 여행 - 일본어
fun getKoreaTravelChecklistJa(): List<ChecklistItem> = listOf(
    ChecklistItem(title = "パスポート（有効期限6ヶ月以上）", category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "航空券予約確認書",              category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "宿泊予約確認書",               category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "海外旅行保険証書",              category = ChecklistCategory.DOCUMENT, destination = "KR"),
    ChecklistItem(title = "ウォン両替",                   category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "海外決済可能なカード",           category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "T-moneyカード（交通用）",       category = ChecklistCategory.MONEY,    destination = "KR"),
    ChecklistItem(title = "220V変圧器（韓国の電圧）",      category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "スマートフォン充電器",           category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "モバイルバッテリー",            category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "ポケットWi-Fi または SIM",     category = ChecklistCategory.ELECTRONIC, destination = "KR"),
    ChecklistItem(title = "現地の天気に合った服",           category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "歩きやすい靴",                 category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "傘またはレインコート",           category = ChecklistCategory.CLOTHING, destination = "KR"),
    ChecklistItem(title = "常備薬（頭痛薬、胃薬）",        category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "マスク",                      category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "手指消毒剤",                  category = ChecklistCategory.HEALTH,   destination = "KR"),
    ChecklistItem(title = "韓国語翻訳アプリのインストール", category = ChecklistCategory.MISC,     destination = "KR"),
    ChecklistItem(title = "Googleマップのオフライン保存",  category = ChecklistCategory.MISC,     destination = "KR"),
    ChecklistItem(title = "緊急連絡先のメモ",             category = ChecklistCategory.MISC,     destination = "KR"),
)