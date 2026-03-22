package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// WeatherViewModel
// UI State 관리 + Repository 호출

class WeatherViewModel: ViewModel() {

    private val repository = WeatherRepository()

    // UI State: 화면에 보여줄 데이터 상태
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchDualCityWeather()
    }

    // KR + JP 날씨 동시 호출
    fun fetchDualCityWeather(

    )
}

// WeatherUiState
// UI 상태를 3가지로 구분 (Loading / Success / Error)

sealed class WeatherUiState {

    //로딩 중
    data object Loading: WeatherUiState()

    // 성공 — KR + JP 날씨 데이터 보유
    data class Success(
        val krWeather: WeatherResponse,
        val jpWeather: WeatherResponse
    ) : WeatherUiState()

    // 실패 — 에러 메시지 보유
    data class Error(
        val message: String
    ) : WeatherUiState()
}