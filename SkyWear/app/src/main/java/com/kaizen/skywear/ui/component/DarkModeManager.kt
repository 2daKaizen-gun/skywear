package com.kaizen.skywear.ui.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaizen.skywear.ui.viewmodel.CitySearchViewModel

// DataStore 저장된 다크 모드 설정 + 시스템 설정 연동
// CitySearchViewModel의 isDarkMode StateFlow 구독

// 현재 다크모드 여부 반환
// DataStore 우선, 없으면 시스템 설정 따름
@Composable
fun rememberDarkMode(viewModel: CitySearchViewModel): Boolean {
    val savedDarkMode by viewModel.isDarkMode.collectAsState()
    val systemDarkMode = isSystemInDarkTheme()

    // DataStore에 저장된 값 우선 적용
    return savedDarkMode ?: systemDarkMode
}