package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.domain.OutfitRecommendation
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
    viewModel: WeatherViewModel
) {
   val uiState by viewModel.uiState.collectAsState()
   val colors = LocalExtraColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.dashboard_refresh))
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_title))
                    }
                    IconButton(onClick = onNavigateToChecklist) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.checklist_title))
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
                            text = stringResource(R.string.dashboard_loading),
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
                            state.krWeather.weather.firstOrNull()?.description,
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
                            state.jpWeather.weather.firstOrNull()?.description,
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
                                    text = stringResource(R.string.dashboard_temp_gap),
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
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.dashboard_checklist_button))
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
                            Text(stringResource(R.string.dashboard_retry))
                        }
                    }
                }
            }
        }
    }
}

// 날씨 카드 컴포넌트
@Composable
private fun WeatherCard(
    modifier: Modifier = Modifier,
    flag: String,
    cityName: String,
    temp: Int,
    weatherDesc: String,
    outfit: OutfitRecommendation,
    feelsLike: Int,
    humidity: Int,
    tempColor: androidx.compose.ui.graphics.Color,
    cardColor: androidx.compose.ui.graphics.Color,
    onCardColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 국기 + 도시명
            Text(
                text = "$flag $cityName",
                style = MaterialTheme.typography.labelLarge,
                color = onCardColor
            )

            // 온도
            Text(
                text = "$temp°",
                style = MaterialTheme.typography.displayMedium,
                color = tempColor
            )

            // 날씨 설명
            Text(
                text = weatherDesc,
                style = MaterialTheme.typography.labelSmall,
                color = onCardColor
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = onCardColor.copy(alpha = 0.2f)
            )

            // 코디 추천
            Text(
                text = outfit.mainOutfit,
                style = MaterialTheme.typography.bodySmall,
                color = onCardColor
            )

            // 체감온도 + 습도
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_feels_like, feelsLike),
                    style = MaterialTheme.typography.labelSmall,
                    color = onCardColor.copy(alpha = 0.7f)
                )
                Text(
                    text = stringResource(R.string.dashboard_humidity, humidity),
                    style = MaterialTheme.typography.labelSmall,
                    color = onCardColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// 코디 전환 요약 카드
@Composable
private fun OutfitTransitionCard(
    krOutfit: OutfitRecommendation,
    jpOutfit: OutfitRecommendation
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🇰🇷", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = krOutfit.emoji, style = MaterialTheme.typography.headlineSmall)
                Text(text = krOutfit.mainOutfit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Text("→", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🇯🇵", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = jpOutfit.emoji, style = MaterialTheme.typography.headlineSmall)
                Text(text = jpOutfit.mainOutfit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}