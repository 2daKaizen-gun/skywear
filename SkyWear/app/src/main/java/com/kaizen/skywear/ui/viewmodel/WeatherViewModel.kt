package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        krCity: String = Constants.DEFAULT_CITY_KR,
        jpCity: String = Constants.DEFAULT_CITY_JP
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            val result = repository.getDualCityWeather(krCity, jpCity)

            _uiState.value = if (result.isSuccess) {
                WeatherUiState.Success(
                    krWeather = result.krWeather.getOrNull()!!,
                    jpWeather = result.jpWeather.getOrNull()!!
                )
            } else {
                WeatherUiState.Error(
                    message = result.errorMessage ?: "알 수 없는 오류 발생."
                )
            }
        }
    }

    // JP 도시 변경(도시 검색 기능)
    fun changeJpCity(cityName: String) {
        fetchDualCityWeather(jpCity = cityName)
    }
}

// WeatherUiState
// UI 상태를 3가지로 구분 (Loading / Success / Error)

sealed class WeatherUiState {

    //로딩 중
    data object Loading: WeatherUiState()

    // 성공: KR + JP 날씨 데이터 보유
    data class Success(
        val krWeather: WeatherResponse,
        val jpWeather: WeatherResponse
    ) : WeatherUiState()

    // 실패: 에러 메시지 보유
    data class Error(
        val message: String
    ) : WeatherUiState()
}