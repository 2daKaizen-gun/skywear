package com.kaizen.skywear.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscribedCityDao {

    @Query("SELECT * FROM subscribed_cities ORDER BY addedAt ASC")
    fun getAllCities(): Flow<List<SubscribedCity>>

    @Query("SELECT * FROM subscribed_cities WHERE isAlertOn = 1")
    suspend fun getAlertEnabledCities(): List<SubscribedCity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: SubscribedCity)

    @Delete
    suspend fun deleteCity(city: SubscribedCity)

    @Query("UPDATE subscribed_cities SET isAlertOn = :isOn WHERE nameEn = :nameEn")
    suspend fun updateAlert(nameEn: String, isOn: Boolean)

    @Query("UPDATE subscribed_cities SET lastTemp = :temp, lastWeatherDesc = :desc WHERE nameEn = :nameEn")
    suspend fun updateLastWeather(nameEn: String, temp: Double, desc: String)

    @Query("SELECT COUNT(*) FROM subscribed_cities")
    suspend fun count(): Int
}