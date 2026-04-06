package com.kaizen.skywear.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kaizen.skywear.data.repository.UserPreferencesRepository

// 도시 검색 + 사용자 설정 상태 관리

class CitySearchViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepository = UserPreferencesRepository(application)

    // 저장된 한국 도시


    // 저장된 일본 도시


    // 한국 검색어


    // 일본 검색어


    // 한국 검색 결과


    // 일본 검색 결과


    // 한국 도시 검색


    // 일본 도시 검색


    // 한국 도시 선택 -> DataStore 저장 -> WeatherViewModel에 전달


    // 일본 도시 선택 -> DataStore 저장 -> WeatherViewModel에 전달


    // 다크 모드 토글


}