package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.local.SubscribedCity
import com.kaizen.skywear.data.model.City
import com.kaizen.skywear.data.model.searchCities
import com.kaizen.skywear.data.repository.SubscribeRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val repository: SubscribeRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val subscribedCities: StateFlow<List<SubscribedCity>> = repository.getAllCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<City>>(emptyList())
    val searchResults: StateFlow<List<City>> = _searchResults.asStateFlow()

    // 현재 날씨 상태 (도시명 → 날씨 텍스트)
    private val _weatherMap = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())
    val weatherMap: StateFlow<Map<String, Pair<Int, String>>> = _weatherMap.asStateFlow()

    init {
        refreshAllWeather()
    }

    fun searchCity(query: String) {
        _searchQuery.value = query
        _searchResults.value = if (query.isBlank()) emptyList()
        else (searchCities(query, "KR") + searchCities(query, "JP"))
            .distinctBy { it.nameEn }
            .take(8)
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            if (repository.count() >= 5) return@launch  // 최대 5개
            repository.addCity(
                SubscribedCity(
                    nameEn  = city.nameEn,
                    nameKo  = city.nameKo,
                    nameJa  = city.nameJa,
                    country = city.country,
                    emoji   = city.emoji
                )
            )
            _searchQuery.value = ""
            _searchResults.value = emptyList()
            fetchWeatherForCity(city.nameEn)
        }
    }

    fun removeCity(city: SubscribedCity) {
        viewModelScope.launch { repository.removeCity(city) }
    }

    fun toggleAlert(city: SubscribedCity) {
        viewModelScope.launch { repository.toggleAlert(city.nameEn, !city.isAlertOn) }
    }

    // 구독 도시 날씨 일괄 갱신
    fun refreshAllWeather() {
        viewModelScope.launch {
            val current = subscribedCities.value
            current.forEach { city -> fetchWeatherForCity(city.nameEn) }
        }
    }

    private suspend fun fetchWeatherForCity(nameEn: String) {
        try {
            val result = weatherRepository.getKrWeather(nameEn)
            result.getOrNull()?.let { weather ->
                val temp = weather.main.temp.toInt()
                val desc = weather.weather.firstOrNull()?.description ?: ""
                repository.updateLastWeather(nameEn, weather.main.temp, desc)
                val map = _weatherMap.value.toMutableMap()
                map[nameEn] = Pair(temp, desc)
                _weatherMap.value = map
            }
        } catch (e: Exception) { /* 조용히 실패 */ }
    }
}