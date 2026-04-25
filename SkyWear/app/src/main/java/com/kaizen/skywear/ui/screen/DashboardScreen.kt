package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.domain.ContextAwareResult
import com.kaizen.skywear.domain.HumidityLevel
import com.kaizen.skywear.domain.OutfitGapLevel
import com.kaizen.skywear.domain.OutfitRecommendation
import com.kaizen.skywear.domain.TempComparisonResult
import com.kaizen.skywear.domain.WindLevel
import com.kaizen.skywear.domain.hasSignificantFeelsLikeDiff
import com.kaizen.skywear.domain.isOutfitDifferent
import com.kaizen.skywear.ui.theme.LocalExtraColors
import com.kaizen.skywear.ui.viewmodel.WeatherUiState
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel
import kotlin.math.abs

// Dual-City 날씨 대시보드 메인 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToChecklist:() -> Unit,
    onNavigateToSearch:() -> Unit,
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val travelDirection by viewModel.travelDirection.collectAsState()
    val colors = LocalExtraColors.current

    // 여헹 방향 따라 출발지, 목적지 결정
    val isKrToJp = travelDirection == TravelDirection.KR_TO_JP

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
                    IconButton(onClick = {
                        viewModel.refresh() }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 여행 방향 스위치 버튼
            TravelDirectionBar(
                direction = travelDirection,
                onToggle = { viewModel.toggleTravelDirection() }
            )
        }

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

                // 방향에 따라 출발지/목적지 스왑
                val departureWeather  = if (isKrToJp) state.krWeather else state.jpWeather
                val destinationWeather = if (isKrToJp) state.jpWeather else state.krWeather
                val departureContext  = if (isKrToJp) state.krContextResult else state.jpContextResult
                val destinationContext = if (isKrToJp) state.jpContextResult else state.krContextResult
                val departureColor    = if (isKrToJp) colors.koreaRed else colors.japanBlue
                val destinationColor  = if (isKrToJp) colors.japanBlue else colors.koreaRed
                val departureFlag     = if (isKrToJp) "🇰🇷" else "🇯🇵"
                val destinationFlag   = if (isKrToJp) "🇯🇵" else "🇰🇷"
                val departureCardColor    = if (isKrToJp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                val destinationCardColor  = if (isKrToJp) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                val departureOnCardColor  = if (isKrToJp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                val destinationOnCardColor = if (isKrToJp) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

                val comparisonMessage = buildComparisonMessage(state.comparisonResult, isKrToJp)
                val travelAdvice      = buildTravelAdvice(state.comparisonResult)
                val contextMessage    = buildContextMessage(departureContext, departureWeather.main.temp)

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
                                    (state.krWeather.weather.firstOrNull()?.description ?: ""),
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
                            weatherDesc = state.jpContextResult.adjustedOutfit.emoji + " " + (state.jpWeather.weather.firstOrNull()?.description ?: ""),
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
                                text = comparisonMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = travelAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // 체감온도 컨텍스트 카드
                    if (state.krContextResult.hasSignificantFeelsLikeDiff(state.krWeather.main.temp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = contextMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
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

// 여행 방향 스위치 바
@Composable
private fun TravelDirectionBar(
    direction: TravelDirection,
    onToggle: () -> Unit
) {
    val label = if (direction == TravelDirection.KR_TO_JP)
        stringResource(R.string.direction_kr_to_jp)
    else
        stringResource(R.string.direction_jp_to_kr)

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.direction_switch_label),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// 비교 메시지 - 방향 반영
@Composable
private fun buildComparisonMessage(result: TempComparisonResult, isKrToJp: Boolean): String {
    return if (isKrToJp) {
        when {
            result.gapDegree > 0 -> stringResource(R.string.comparison_jp_warmer, result.jpCityName, abs(result.gapDegree))
            result.gapDegree < 0 -> stringResource(R.string.comparison_jp_colder, result.jpCityName, abs(result.gapDegree))
            else -> stringResource(R.string.comparison_same_kr_jp, result.jpCityName)
        }
    } else {
        when {
            result.gapDegree < 0 -> stringResource(R.string.comparison_kr_warmer, result.jpCityName, abs(result.gapDegree))
            result.gapDegree > 0 -> stringResource(R.string.comparison_kr_colder, result.jpCityName, abs(result.gapDegree))
            else -> stringResource(R.string.comparison_same_jp_kr, result.jpCityName)
        }
    }
}

// 여행 조인
@Composable
private fun buildTravelAdvice(result: TempComparisonResult): String =
    when (result.outfitGapLevel) {
        OutfitGapLevel.SIMILAR -> when {
            result.jpTemp <= 0  -> stringResource(R.string.advice_similar_freezing)
            result.jpTemp <= 10 -> stringResource(R.string.advice_similar_cold)
            result.jpTemp <= 20 -> stringResource(R.string.advice_similar_mild)
            else                -> stringResource(R.string.advice_similar_hot)
        }
        OutfitGapLevel.MODERATE -> when {
            result.gapDegree > 0 -> stringResource(R.string.advice_moderate_warmer)
            else                 -> stringResource(R.string.advice_moderate_colder)
        }
        OutfitGapLevel.SIGNIFICANT -> when {
            result.gapDegree > 0 -> stringResource(R.string.advice_significant_warmer)
            else                 -> stringResource(R.string.advice_significant_colder)
        }
    }

// 체감온도 컨텍스트 메시지
@Composable
private fun buildContextMessage(result: ContextAwareResult, actualTemp: Double): String {
    val tempDiff = actualTemp - result.feelsLikeTemp
    return when {
        result.windLevel == WindLevel.VERY_WINDY && tempDiff >= 5 ->
            stringResource(R.string.context_very_windy_cold)
        result.windLevel == WindLevel.WINDY && tempDiff >= 3 ->
            stringResource(R.string.context_windy_cold)
        result.humidityLevel == HumidityLevel.VERY_HUMID && result.feelsLikeTemp > actualTemp ->
            stringResource(R.string.context_very_humid_hot)
        result.humidityLevel == HumidityLevel.HUMID ->
            stringResource(R.string.context_humid)
        result.humidityLevel == HumidityLevel.DRY ->
            stringResource(R.string.context_dry)
        else ->
            stringResource(R.string.context_comfortable)
    }
}

// Outfit stage → stringResource
@Composable
fun OutfitRecommendation.localizedMainOutfit(): String = stringResource(
    when (stage) {
        1    -> R.string.outfit_stage1
        2    -> R.string.outfit_stage2
        3    -> R.string.outfit_stage3
        4    -> R.string.outfit_stage4
        5    -> R.string.outfit_stage5
        6    -> R.string.outfit_stage6
        7    -> R.string.outfit_stage7
        else -> R.string.outfit_stage8
    }
)

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
                outfit.localizedMainOutfit(),
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
                Text(krOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Text("→", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🇯🇵", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = jpOutfit.emoji, style = MaterialTheme.typography.headlineSmall)
                Text(text = jpOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}