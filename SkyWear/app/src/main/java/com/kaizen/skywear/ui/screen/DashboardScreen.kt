package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.domain.isOutfitDifferent
import com.kaizen.skywear.ui.theme.LocalExtraColors
import com.kaizen.skywear.ui.viewmodel.WeatherUiState
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

            // 로딩
            is WeatherUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "날씨 정보를 불러오는 중..",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 성공
            is WeatherUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Dual-City 날씨 카드
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // KR 카드
                        WeatherCard(
                            modifier = Modifier.weight(1f),
                            flag = "🇰🇷",
                            cityName = state.krWeather.cityName,
                            temp = state.krWeather.tempRounded(),
                            weatherDesc = state.krContextResult.adjustedOutfit.emoji + " " +
                            state.krWeather.weather.firstOrNull()?.description ?: "",
                            outfit = state.krContextResult.adjustedOutfit,
                            feelsLike = state.krContextResult.feelsLikeTemp.toInt(),
                            humidity = state.krWeather.main.humidity,
                            tempColor = colors.koreaRed,
                            cardColor = MaterialTheme.colorScheme.primaryContainer,
                            onCardColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        // JP 카드
                        WeatherCard(
                            modifier = Modifier.weight(1f),
                            flag = "🇯🇵",
                            cityName = state.jpWeather.cityName,
                            temp = state.jpWeather.tempRounded(),
                            weatherDesc = state.jpContextResult.adjustedOutfit.emoji + " " +
                            state.jpWeather.weather.firstOrNull()?.description ?: "",
                            outfit = state.jpContextResult.adjustedOutfit,
                            feelsLike = state.jpContextResult.feelsLikeTemp.toInt(),
                            humidity = state.jpWeather.main.humidity,
                            tempColor = colors.japanBlue,
                            cardColor = MaterialTheme.colorScheme.secondaryContainer,
                            onCardColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // 온도 차이 뱃지
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "온도 차이",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = state.comparisonResult.gapLabel,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.comparisonResult.comparisonMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.comparisonResult.travelAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // 코디 전환 요약
                    if (state.comparisonResult.isOutfitDifferent()) {
                        OutfitTransitionCard(
                            krOutfit = state.comparisonResult.krOutfit,
                            jpOutfit = state.comparisonResult.jpOutfit
                        )
                    }

                    // 체크리스트 바로가기 버튼
                    Button(
                        onClick = onNavigateToChecklist,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Checklist, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("일본 여행 체크리스트 보기")
                    }
                }
            }

            // 에러
            is WeatherUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.displaySmall
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text("다시 시도")
                        }
                    }
                }
            }
        }
    }
}

// 날씨 카드 컴포넌트
