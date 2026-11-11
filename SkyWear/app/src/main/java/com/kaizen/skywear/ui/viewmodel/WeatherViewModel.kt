package com.kaizen.skywear.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.model.DailyForecastPair
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.dailyRepresentativeWithTime
import com.kaizen.skywear.data.model.dateKey
import com.kaizen.skywear.data.model.dateLabel
import com.kaizen.skywear.data.remote.toErrorMessageRes
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.domain.ContextAwareResult
import com.kaizen.skywear.domain.TempComparisonResult
import com.kaizen.skywear.domain.analyzeTempComparison
import com.kaizen.skywear.domain.buildContextAwareRecommendation
import com.kaizen.skywear.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
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

    // 예보 상태
    private val _forecastState = MutableStateFlow<ForecastUiState>(ForecastUiState.Loading)
    val forecastState: StateFlow<ForecastUiState> = _forecastState.asStateFlow()

    // 선택된 날짜(null 현재 날씨 탭)
    private val _selectedDateKey = MutableStateFlow<String?>(null)
    val selectedDateKey: StateFlow<String?> = _selectedDateKey.asStateFlow()

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
            fetchAll(savedKr, savedJp)
        }
    }

    // 새로고침
    fun refresh() {
        fetchAll(_selectedKrCity.value, _selectedJpCity.value)
    }

    // 헌재 날씨 + 예보 동시 호출
    private fun fetchAll(krCity: String, jpCity: String) {
        fetchDualCityWeather(krCity, jpCity)
        fetchDualCityForecast(krCity, jpCity)
    }
    
    // 현재 날씨 호출
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
                WeatherUiState.Success(
                    krWeather        = krWeather,
                    jpWeather        = jpWeather,
                    krContextResult  = buildContextAwareRecommendation(krWeather),
                    jpContextResult  = buildContextAwareRecommendation(jpWeather),
                    comparisonResult = analyzeTempComparison(krWeather, jpWeather)
                )
            } else {
                WeatherUiState.Error(result.error.toErrorMessageRes())
            }
        }
    }
    
    // 5일 예보 호출
    fun fetchDualCityForecast(
        krCity: String = _selectedKrCity.value,
        jpCity: String = _selectedJpCity.value
    ) {
        viewModelScope.launch {
            _forecastState.value = ForecastUiState.Loading
            val result = repository.getDualCityForecast(krCity, jpCity)
            _forecastState.value = if (result.isSuccess) {
                val krForecast = result.krForecast.getOrNull()!!
                val jpForecast = result.jpForecast.getOrNull()!!

                // 각 도시의 현지 시각 기준 (API dt_txt는 UTC)
                val krOffset = krForecast.city.utcOffsetSeconds
                val jpOffset = jpForecast.city.utcOffsetSeconds

                // 대표 슬롯, 시간, pair(최저, 최고)
                val krDaily = krForecast.list.dailyRepresentativeWithTime(krOffset)
                val jpDaily = jpForecast.list.dailyRepresentativeWithTime(jpOffset)

                // KR + JP 공통 날짜만 페어링
                val today = LocalDate.now(ZoneOffset.ofTotalSeconds(krOffset)).toString()
                val commonDates = krDaily.keys.intersect(jpDaily.keys)
                    .filter { it >= today }
                    .filter { key ->
                        // KR, JP 모두 슬롯 3개 이상인 날짜만
                        krForecast.list.count { it.dateKey(krOffset) == key } >= 3 &&
                            jpForecast.list.count { it.dateKey(jpOffset) == key } >= 3
                    }
                    .sorted()
                val pairs = commonDates.mapNotNull { dateKey ->
                    val (krItem, krTime, krMinMax) = krDaily[dateKey] ?: return@mapNotNull null
                    val (jpItem, _, jpMinMax)      = jpDaily[dateKey] ?: return@mapNotNull null
                    DailyForecastPair(
                        dateKey   = dateKey,
                        dateLabel = krItem.dateLabel(krOffset),
                        representativeTime = krTime,
                        krItem    = krItem,
                        jpItem    = jpItem,
                        krTempMin = krMinMax.first,
                        krTempMax = krMinMax.second,
                        jpTempMin = jpMinMax.first,
                        jpTempMax = jpMinMax.second
                    )
                }
                ForecastUiState.Success(pairs = pairs)
            } else {
                ForecastUiState.Error
            }
        }
    }

    // 날짜 선택
    // null = 현재 날씨, dateKey = 예보 날짜
    fun selectDate(dateKey: String?) {
        _selectedDateKey.value = dateKey
    }

    // KR 도시 변경
    fun changeKrCity(cityName: String) {
        _selectedKrCity.value = cityName
        viewModelScope.launch {
            prefsRepository.saveKrCity(cityName)
        }
        fetchAll(krCity = cityName, jpCity = _selectedJpCity.value)
    }

    // JP 도시 변경(도시 검색 기능)
    fun changeJpCity(cityName: String) {
        _selectedJpCity.value = cityName
        viewModelScope.launch {
            prefsRepository.saveJpCity(cityName)
        }
        fetchAll(krCity = _selectedKrCity.value, jpCity = cityName)
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
        val comparisonResult: TempComparisonResult // KRvsJP 비교 분석 결과
    ) : WeatherUiState()

    // 실패: 현지화된 에러 메시지 리소스 보유
    data class Error(
        @StringRes val messageRes: Int
    ) : WeatherUiState()
}

sealed class ForecastUiState {
    data object Loading : ForecastUiState()
    data class Success(val pairs: List<DailyForecastPair>) : ForecastUiState()
    data object Error : ForecastUiState()
}