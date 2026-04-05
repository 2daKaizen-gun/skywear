package com.kaizen.skywear.data.model

// 사전 정의된 한국/일본 주요 도시 리스트
// 도시 검색 기능에서 활용됨

data class City(
    val nameEn: String, // API 호출용
    val nameKo: String, // UI 표시용
    val country: String, // 국가 코드
    val emoji: String // 국기 이모지
)

// 한국 주요 도시 리스트
val KR_CITIES = listOf(
    City("Seoul", "서울", "KR", "🇰🇷"),
    City("Busan", "부산", "KR", "🇰🇷"),
    City("Incheon",  "인천",  "KR", "🇰🇷"),
    City("Daegu",    "대구",  "KR", "🇰🇷"),
    City("Daejeon",  "대전",  "KR", "🇰🇷"),
    City("Gwangju",  "광주",  "KR", "🇰🇷"),
    City("Suwon",    "수원",  "KR", "🇰🇷"),
    City("Jeju",     "제주",  "KR", "🇰🇷"),
)

// 일본 주요 도시 리스트
val JP_CITIES = listOf(
    City("Osaka",     "오사카", "JP", "🇯🇵"),
    City("Tokyo",     "도쿄",  "JP", "🇯🇵"),
    City("Kyoto",     "교토",  "JP", "🇯🇵"),
    City("Fukuoka",   "후쿠오카","JP","🇯🇵"),
    City("Sapporo",   "삿포로", "JP", "🇯🇵"),
    City("Nagoya",    "나고야", "JP", "🇯🇵"),
    City("Hiroshima", "히로시마","JP","🇯🇵"),
    City("Nara",      "나라",  "JP", "🇯🇵"),
    City("Kobe",      "고베",  "JP", "🇯🇵"),
    City("Yokohama",  "요코하마","JP","🇯🇵"),
)

// 도시명 검색
fun searchCities(query: String, country: String): List<City> {
    if (query.isBlank()) return if (country == "KR") KR_CITIES else JP_CITIES

    val cities = if (country == "KR") KR_CITIES else JP_CITIES
    return cities.filter {
        it.nameKo.contains(query, ignoreCase = true) || it.nameEn.contains(query, ignoreCase = true)
    }
}