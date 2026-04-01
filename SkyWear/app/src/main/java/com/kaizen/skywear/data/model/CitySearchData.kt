package com.kaizen.skywear.data.model

import androidx.room.Query

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

)

// 일본 주요 도시 리스트
val JP_CITIES = listOf(

)

// 도시명 검색
fun searchCities(query: String, country: String): List<City> {

}