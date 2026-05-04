package com.kaizen.skywear.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 구독 도시 Room Entity
@Entity(tableName = "subscribed_cities")
data class SubscribedCity(
    @PrimaryKey val nameEn: String,
    val nameKo: String,
    val nameJa: String,
    val country: String,
    val emoji: String,
    val isAlertOn: Boolean = true,
    val lastTemp: Double = 0.0,
    val lastWeatherDesc: String = "",
    val addedAt: Long = System.currentTimeMillis()
)