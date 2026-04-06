package com.kaizen.skywear.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.model.City
import com.kaizen.skywear.data.model.JP_CITIES
import com.kaizen.skywear.data.model.KR_CITIES
import com.kaizen.skywear.data.model.searchCities
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 도시 검색 + 사용자 설정 상태 관리

class CitySearchViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepository = UserPreferencesRepository(application)

    // 저장된 한국 도시
    val savedKrCity: StateFlow<String> = prefsRepository.selectedKrCity
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Seoul"
        )

    // 저장된 일본 도시
    val savedJpCity: StateFlow<String> = prefsRepository.selectedJpCity
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Osaka"
        )

    // 다크 모드 설정
    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // 한국 검색어
    private val _krSearchQuery = MutableStateFlow("")
    val krSearchQuery: StateFlow<String> = _krSearchQuery.asStateFlow()

    // 일본 검색어
    private val _jpSearchQuery = MutableStateFlow("")
    val jpSearchQuery: StateFlow<String> = _jpSearchQuery.asStateFlow()

    // 한국 검색 결과
    private val _krSearchResults = MutableStateFlow<List<City>>(KR_CITIES)
    val krSearchResults: StateFlow<List<City>> = _krSearchResults.asStateFlow()

    // 일본 검색 결과
    private val _jpSearchResults = MutableStateFlow<List<City>>(JP_CITIES)
    val jpSearchResults: StateFlow<List<City>> = _jpSearchResults.asStateFlow()

    // 한국 도시 검색
    fun searchKrCity(query: String) {
        _krSearchQuery.value = query
        _krSearchResults.value = searchCities(query, "KR")
    }

    // 일본 도시 검색
    fun searchJpCity(query: String) {
        _jpSearchQuery.value = query
        _jpSearchResults.value = searchCities(query, "JP")
    }

    // 한국 도시 선택 -> DataStore 저장 -> WeatherViewModel에 전달


    // 일본 도시 선택 -> DataStore 저장 -> WeatherViewModel에 전달


    // 다크 모드 토글
    fun toggleDarkMode() {
        viewModelScope.launch {
            prefsRepository.saveDarkMode(!isDarkMode.value)
        }
    }
}