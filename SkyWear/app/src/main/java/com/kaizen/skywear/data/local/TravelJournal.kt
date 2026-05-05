package com.kaizen.skywear.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// TravelJournal — 여행 일지 Room Entity
@Entity(tableName = "travel_journals")
data class TravelJournal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destination: String,        // "Osaka", "Tokyo" 등
    val destinationEmoji: String,   // 🇯🇵 / 🇰🇷
    val startDate: String,          // "2025-03-10"
    val endDate: String,            // "2025-03-14"
    val departTemp: Int,            // 출발일 기온
    val departWeatherDesc: String,  // 출발일 날씨 설명
    val departOutfit: String,       // 출발일 코디
    val returnTemp: Int,            // 귀국일 기온
    val returnWeatherDesc: String,
    val returnOutfit: String,
    val memo: String = "",          // 여행 메모
    val createdAt: Long = System.currentTimeMillis()
)