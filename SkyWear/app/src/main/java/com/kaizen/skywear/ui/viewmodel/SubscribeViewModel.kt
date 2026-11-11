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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

// 구독 가능한 최대 도시 수
const val MAX_SUBSCRIBED_CITIES = 5

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
            if (repository.count() >= MAX_SUBSCRIBED_CITIES) return@launch
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
            // subscribedCities(stateIn)는 화면이 구독하기 전엔 빈 리스트라 init 시점에 항상 비어 있음 → DB에서 직접 조회
            val current = repository.getAllCities().first()
            // 도시별 요청을 병렬로 실행
            current.forEach { city -> launch { fetchWeatherForCity(city.nameEn) } }
        }
    }

    private suspend fun fetchWeatherForCity(nameEn: String) {
        try {
            val result = weatherRepository.getWeather(nameEn)
            result.getOrNull()?.let { weather ->
                // 대시보드/알림과 같은 값이 보이도록 절삭(toInt) 대신 반올림
                val temp = weather.main.temp.roundToInt()
                val desc = weather.weather.firstOrNull()?.description ?: ""
                repository.updateLastWeather(nameEn, weather.main.temp, desc)
                _weatherMap.update { it + (nameEn to Pair(temp, desc)) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) { /* 조용히 실패 */ }
    }
}