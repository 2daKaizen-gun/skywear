package com.kaizen.skywear.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.ui.theme.LocalExtraColors
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel

// Dual-City 날씨 대시보드 메인 화면

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToChecklist:() -> Unit,
    onNavigateToSearch:() -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
   val uiState by viewModel.uiState.collectAsState()
   val colors = LocalExtraColors.current




}