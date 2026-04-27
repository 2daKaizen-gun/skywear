package com.kaizen.skywear.data.model

import java.util.Locale

// 사전 정의된 한국/일본 주요 도시 리스트
// 도시 검색 기능에서 활용됨

data class City(
    val nameEn: String, // API 호출용
    val nameKo: String, // 한국어 표시
    val nameJa: String, // 일본어 표시
    val country: String, // 국가 코드
    val emoji: String // 국기 이모지
) {
    // 기기 언어에 맞는 이름 반환
    fun localizedName(): String = when (Locale.getDefault().language) {
        "ja" -> nameJa
        "ko" -> nameKo
        else -> nameEn
    }
}

// 한국 주요 도시 리스트
val KR_CITIES = listOf(
    City("Seoul",   "서울",  "ソウル",    "KR", "🇰🇷"),
    City("Busan",   "부산",  "プサン",    "KR", "🇰🇷"),
    City("Incheon", "인천",  "インチョン", "KR", "🇰🇷"),
    City("Daegu",   "대구",  "テグ",      "KR", "🇰🇷"),
    City("Daejeon", "대전",  "テジョン",  "KR", "🇰🇷"),
    City("Gwangju", "광주",  "クァンジュ", "KR", "🇰🇷"),
    City("Suwon",   "수원",  "スウォン",  "KR", "🇰🇷"),
    City("Jeju",    "제주",  "チェジュ",  "KR", "🇰🇷"),
)

// 일본 주요 도시 리스트
val JP_CITIES = listOf(
    City("Osaka",     "오사카",  "大阪",   "JP", "🇯🇵"),
    City("Tokyo",     "도쿄",    "東京",   "JP", "🇯🇵"),
    City("Kyoto",     "교토",    "京都",   "JP", "🇯🇵"),
    City("Fukuoka",   "후쿠오카", "福岡",   "JP", "🇯🇵"),
    City("Sapporo",   "삿포로",  "札幌",   "JP", "🇯🇵"),
    City("Nagoya",    "나고야",  "名古屋",  "JP", "🇯🇵"),
    City("Hiroshima", "히로시마", "広島",   "JP", "🇯🇵"),
    City("Nara",      "나라",    "奈良",   "JP", "🇯🇵"),
    City("Kobe",      "고베",    "神戸",   "JP", "🇯🇵"),
    City("Yokohama",  "요코하마", "横浜",   "JP", "🇯🇵"),
)

val ALL_CITIES = KR_CITIES + JP_CITIES

// API 영어 응답 → 현지화된 도시명 변환
// ex) "Seoul" → "서울" (한국어), "ソウル" (일본어)
fun localizedCityName(nameEn: String): String {
    val city = ALL_CITIES.find {
        it.nameEn.equals(nameEn, ignoreCase = true)
    }
    return city?.localizedName() ?: nameEn  // 없으면 영어 그대로
}

fun searchCities(query: String, country: String): List<City> {
    if (query.isBlank()) return if (country == "KR") KR_CITIES else JP_CITIES
    val cities = if (country == "KR") KR_CITIES else JP_CITIES
    return cities.filter {
        it.nameKo.contains(query, ignoreCase = true) ||
                it.nameEn.contains(query, ignoreCase = true) ||
                it.nameJa.contains(query, ignoreCase = true)
    }
}