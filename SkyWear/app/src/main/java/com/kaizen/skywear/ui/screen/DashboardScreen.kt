package com.kaizen.skywear.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "\uD83C\uDF24\uFE0F SkyWear",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "도서 검색")
                    }
                    IconButton(onClick = onNavigateToChecklist) {
                        Icon(Icons.Default.Checklist, contentDescription = "체크 리스트")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {

        }
    }


}