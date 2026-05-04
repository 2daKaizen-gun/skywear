package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.local.SubscribedCity
import com.kaizen.skywear.data.local.SubscribedCityDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeRepository @Inject constructor(
    private val dao: SubscribedCityDao
) {
    fun getAllCities(): Flow<List<SubscribedCity>> = dao.getAllCities()
    suspend fun addCity(city: SubscribedCity) = dao.insertCity(city)
    suspend fun removeCity(city: SubscribedCity) = dao.deleteCity(city)
    suspend fun toggleAlert(nameEn: String, isOn: Boolean) = dao.updateAlert(nameEn, isOn)
    suspend fun updateLastWeather(nameEn: String, temp: Double, desc: String) =
        dao.updateLastWeather(nameEn, temp, desc)
    suspend fun count() = dao.count()
    suspend fun getAlertEnabledCities() = dao.getAlertEnabledCities()
}