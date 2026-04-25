package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.iconCode
import com.kaizen.skywear.data.model.weatherId
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.domain.ContextAwareResult
import com.kaizen.skywear.domain.TempComparisonResult
import com.kaizen.skywear.domain.WeatherVisual
import com.kaizen.skywear.domain.analyzeTempComparison
import com.kaizen.skywear.domain.buildContextAwareRecommendation
import com.kaizen.skywear.domain.mapWeatherCodeToVisual
import com.kaizen.skywear.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// WeatherViewModel Update
// UI State 관리 + Repository 호출

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val prefsRepository: UserPreferencesRepository
): ViewModel() {

    // UI State: 화면에 보여줄 데이터 상태
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // 현재 선택된 KR 도시
    private val _selectedKrCity = MutableStateFlow(Constants.DEFAULT_CITY_KR)
    val selectedKrCity: StateFlow<String> = _selectedKrCity.asStateFlow()

    // 현재 선택된 JP 도시 (도시 검색 활용)
    private val _selectedJpCity = MutableStateFlow(Constants.DEFAULT_CITY_JP)
    val selectedJpCity: StateFlow<String> = _selectedJpCity.asStateFlow()

    // 여행 방향 StateFlow
    val travelDirection: StateFlow<TravelDirection> = prefsRepository.travelDirection
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TravelDirection.KR_TO_JP
        )

    init {
        viewModelScope.launch {
            // DataStore에서 저장된 도시 먼저 로드
            val savedKr = prefsRepository.selectedKrCity.first()
            val savedJp = prefsRepository.selectedJpCity.first()
            _selectedKrCity.value = savedKr
            _selectedJpCity.value = savedJp
            fetchDualCityWeather(savedKr, savedJp)
        }
    }

    // 새로고침
    fun refresh() {
        fetchDualCityWeather(
            krCity = _selectedKrCity.value,
            jpCity = _selectedJpCity.value
        )
    }

    // KR 도시 변경
    fun changeKrCity(cityName: String) {
        _selectedKrCity.value = cityName
        viewModelScope.launch {
            prefsRepository.saveKrCity(cityName)
        }
        fetchDualCityWeather(krCity = cityName)
    }

    // JP 도시 변경(도시 검색 기능)
    fun changeJpCity(cityName: String) {
        _selectedJpCity.value = cityName
        viewModelScope.launch {
            prefsRepository.saveJpCity(cityName)
        }
        fetchDualCityWeather(jpCity = cityName)
    }

    // 여행 방향 토글
    fun toggleTravelDirection() {
        viewModelScope.launch {
            val current = travelDirection.value
            val next = if (current == TravelDirection.KR_TO_JP)
                TravelDirection.JP_TO_KR
            else
                TravelDirection.KR_TO_JP
            prefsRepository.saveTravelDirection(next)
        }
    }

    // KR + JP 날씨 동시 호출 -> 코디/ 비교 분석까지 한번에 처리
    fun fetchDualCityWeather(
        krCity: String = _selectedKrCity.value,
        jpCity: String = _selectedJpCity.value
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val result = repository.getDualCityWeather(krCity, jpCity)

            _uiState.value = if (result.isSuccess) {
                val krWeather = result.krWeather.getOrNull()!!
                val jpWeather = result.jpWeather.getOrNull()!!

                // phase 3 로직 통합 실행
                WeatherUiState.Success(
                    krWeather = krWeather,
                    jpWeather = jpWeather,
                    krContextResult = buildContextAwareRecommendation(krWeather),
                    jpContextResult = buildContextAwareRecommendation(jpWeather),
                    comparisonResult = analyzeTempComparison(krWeather, jpWeather),

                    krVisual = mapWeatherCodeToVisual(
                        weatherId = krWeather.weatherId(),
                        iconCode = krWeather.iconCode(),
                        temp = krWeather.main.temp
                    ),

                    jpVisual = mapWeatherCodeToVisual(
                        weatherId = jpWeather.weatherId(),
                        iconCode = jpWeather.iconCode(),
                        temp = jpWeather.main.temp
                    )
                )
            } else {
                WeatherUiState.Error(
                    message = result.errorMessage ?: "알 수 없는 오류 발생."
                )
            }
        }
    }
}
// WeatherUiState Update
// UI 상태를 3가지로 구분 (Loading / Success / Error)

sealed class WeatherUiState {

    //로딩 중
    data object Loading: WeatherUiState()

    // 성공: 날씨, 코디, 비교, 체감온도 전부 포함
    data class Success(
        val krWeather: WeatherResponse, // kr 날씨 원본 데이터
        val jpWeather: WeatherResponse, // jp 날씨 원본 데이터
        val krContextResult: ContextAwareResult, // kr 체감온도/코디 보정 결과
        val jpContextResult: ContextAwareResult, // jp 체감온도/코디 보정 결과
        val comparisonResult: TempComparisonResult, // KRvsJP 비교 분석 결과

        val krVisual: WeatherVisual,
        val jpVisual: WeatherVisual
    ) : WeatherUiState()

    // 실패: 에러 메시지 보유
    data class Error(
        val message: String
    ) : WeatherUiState()
}