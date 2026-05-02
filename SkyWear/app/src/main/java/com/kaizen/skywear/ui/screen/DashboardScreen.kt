package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
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
import com.kaizen.skywear.data.model.DailyForecastPair
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.domain.ContextAwareResult
import com.kaizen.skywear.domain.HumidityLevel
import com.kaizen.skywear.domain.OutfitGapLevel
import com.kaizen.skywear.domain.OutfitRecommendation
import com.kaizen.skywear.domain.TempComparisonResult
import com.kaizen.skywear.domain.WindLevel
import com.kaizen.skywear.domain.directedGapLabel
import com.kaizen.skywear.domain.getOutfitRecommendation
import com.kaizen.skywear.domain.hasSignificantFeelsLikeDiff
import com.kaizen.skywear.domain.isOutfitDifferent
import com.kaizen.skywear.ui.theme.LocalExtraColors
import com.kaizen.skywear.ui.viewmodel.ForecastUiState
import com.kaizen.skywear.ui.viewmodel.WeatherUiState
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToChecklist: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: WeatherViewModel
) {
    val uiState         by viewModel.uiState.collectAsState()
    val forecastState   by viewModel.forecastState.collectAsState()
    val travelDirection by viewModel.travelDirection.collectAsState()
    val selectedDateKey by viewModel.selectedDateKey.collectAsState()
    val colors = LocalExtraColors.current
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 여행 방향 스위치 바
            TravelDirectionBar(
                direction = travelDirection,
                onToggle = { viewModel.toggleTravelDirection() }
            )

            // 날짜 선택 탭 (스위치 바 바로 아래)
            DateSelectorRow(
                forecastState = forecastState,
                selectedDateKey = selectedDateKey,
                onDateSelected = { viewModel.selectDate(it) }
            )

            // 선택된 날짜에 따라 현재 날씨 or 예보 표시
            if (selectedDateKey == null) {
                // 현재 날씨
                CurrentWeatherContent(
                    uiState = uiState,
                    isKrToJp = isKrToJp,
                    colors = colors,
                    onNavigateToChecklist = onNavigateToChecklist,
                    onRefresh = { viewModel.refresh() }
                )
            } else {
                // 예보 날짜
                val selectedPair = (forecastState as? ForecastUiState.Success)
                    ?.pairs?.firstOrNull { it.dateKey == selectedDateKey }
                if (selectedPair != null) {
                    ForecastDayContent(
                        pair = selectedPair,
                        isKrToJp = isKrToJp,
                        colors = colors,
                        onNavigateToChecklist = onNavigateToChecklist
                    )
                }
            }
        }
    }
}

// 날짜 선택 탭 (가로 스크롤)
@Composable
private fun DateSelectorRow(
    forecastState: ForecastUiState,
    selectedDateKey: String?,
    onDateSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "지금/Now/現在" 탭 — 현재 날씨
        FilterChip(
            selected = selectedDateKey == null,
            onClick = { onDateSelected(null) },
            label = { Text(stringResource(R.string.forecast_now)) }
        )

        // 예보 날짜 탭
        if (forecastState is ForecastUiState.Success) {
            forecastState.pairs.forEach { pair ->
                FilterChip(
                    selected = selectedDateKey == pair.dateKey,
                    onClick = { onDateSelected(pair.dateKey) },
                    label = {
                        // 날짜 + 대표 슬롯 시간 표시
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = pair.dateLabel,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = pair.representativeTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}

// 현재 날씨 콘텐츠
@Composable
private fun CurrentWeatherContent(
    uiState: WeatherUiState,
    isKrToJp: Boolean,
    colors: com.kaizen.skywear.ui.theme.SkyWearExtraColors,
    onNavigateToChecklist: () -> Unit,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is WeatherUiState.Loading -> LoadingContent()
        is WeatherUiState.Success -> {
            val departureWeather   = if (isKrToJp) uiState.krWeather else uiState.jpWeather
            val destinationWeather = if (isKrToJp) uiState.jpWeather else uiState.krWeather
            val departureContext   = if (isKrToJp) uiState.krContextResult else uiState.jpContextResult
            val destinationContext = if (isKrToJp) uiState.jpContextResult else uiState.krContextResult
            val departureColor     = if (isKrToJp) colors.koreaRed else colors.japanBlue
            val destinationColor   = if (isKrToJp) colors.japanBlue else colors.koreaRed
            val departureFlag      = if (isKrToJp) "🇰🇷" else "🇯🇵"
            val destinationFlag    = if (isKrToJp) "🇯🇵" else "🇰🇷"
            val depCardColor       = if (isKrToJp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            val dstCardColor       = if (isKrToJp) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
            val depOnCard          = if (isKrToJp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            val dstOnCard          = if (isKrToJp) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

            val comparisonMessage = buildComparisonMessage(uiState.comparisonResult, isKrToJp)
            val travelAdvice      = buildTravelAdvice(uiState.comparisonResult, isKrToJp)
            val contextMessage    = buildContextMessage(departureContext, departureWeather.main.temp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WeatherCard(
                        modifier = Modifier.weight(1f),
                        flag = departureFlag,
                        cityName = localizedCityName(departureWeather.cityName),
                        temp = departureWeather.tempRounded(),
                        weatherDesc = departureContext.adjustedOutfit.emoji + " " +
                                (departureWeather.weather.firstOrNull()?.description ?: ""),
                        outfit = departureContext.adjustedOutfit,
                        feelsLike = departureContext.feelsLikeTemp.toInt(),
                        humidity = departureWeather.main.humidity,
                        tempColor = departureColor,
                        cardColor = depCardColor,
                        onCardColor = depOnCard
                    )
                    WeatherCard(
                        modifier = Modifier.weight(1f),
                        flag = destinationFlag,
                        cityName = localizedCityName(destinationWeather.cityName),
                        temp = destinationWeather.tempRounded(),
                        weatherDesc = destinationContext.adjustedOutfit.emoji + " " +
                                (destinationWeather.weather.firstOrNull()?.description ?: ""),
                        outfit = destinationContext.adjustedOutfit,
                        feelsLike = destinationContext.feelsLikeTemp.toInt(),
                        humidity = destinationWeather.main.humidity,
                        tempColor = destinationColor,
                        cardColor = dstCardColor,
                        onCardColor = dstOnCard
                    )
                }

                TempGapCard(
                    gapLabel = uiState.comparisonResult.directedGapLabel(isKrToJp),
                    comparisonMessage = comparisonMessage,
                    travelAdvice = travelAdvice
                )

                if (departureContext.hasSignificantFeelsLikeDiff(departureWeather.main.temp)) {
                    ContextCard(contextMessage)
                }

                if (uiState.comparisonResult.isOutfitDifferent()) {
                    OutfitTransitionCard(
                        departureOutfit   = if (isKrToJp) uiState.comparisonResult.krOutfit else uiState.comparisonResult.jpOutfit,
                        destinationOutfit = if (isKrToJp) uiState.comparisonResult.jpOutfit else uiState.comparisonResult.krOutfit,
                        departureFlag = departureFlag,
                        destinationFlag = destinationFlag
                    )
                }

                ChecklistButton(isKrToJp, onNavigateToChecklist)
            }
        }
        is WeatherUiState.Error -> ErrorContent(uiState.message, onRefresh)
    }
}

// 예보 날짜 콘텐츠
@Composable
private fun ForecastDayContent(
    pair: DailyForecastPair,
    isKrToJp: Boolean,
    colors: com.kaizen.skywear.ui.theme.SkyWearExtraColors,
    onNavigateToChecklist: () -> Unit
) {
    val depItem   = if (isKrToJp) pair.krItem else pair.jpItem
    val dstItem   = if (isKrToJp) pair.jpItem else pair.krItem
    val depColor  = if (isKrToJp) colors.koreaRed else colors.japanBlue
    val dstColor  = if (isKrToJp) colors.japanBlue else colors.koreaRed
    val depFlag   = if (isKrToJp) "🇰🇷" else "🇯🇵"
    val dstFlag   = if (isKrToJp) "🇯🇵" else "🇰🇷"
    val depCard   = if (isKrToJp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val dstCard   = if (isKrToJp) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
    val depOnCard = if (isKrToJp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val dstOnCard = if (isKrToJp) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    // 방향에 따른 최저/최고
    val depTempMin = if (isKrToJp) pair.krTempMin else pair.jpTempMin
    val depTempMax = if (isKrToJp) pair.krTempMax else pair.jpTempMax
    val dstTempMin = if (isKrToJp) pair.jpTempMin else pair.krTempMin
    val dstTempMax = if (isKrToJp) pair.jpTempMax else pair.krTempMax

    val depOutfit  = getOutfitRecommendation(depItem.main.temp)
    val dstOutfit  = getOutfitRecommendation(dstItem.main.temp)

    val depTemp     = depItem.main.temp.roundToInt()
    val dstTemp     = dstItem.main.temp.roundToInt()
    val gapDegree   = dstTemp - depTemp
    val gapLabel    = if (gapDegree >= 0) "+${gapDegree}°C" else "${gapDegree}°C"

    val depCityName = localizedCityName(if (isKrToJp) "Seoul" else "Osaka")
    val dstCityName = localizedCityName(if (isKrToJp) "Osaka" else "Seoul")

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 날짜 헤더 — 날짜 + 대표 슬롯 시간 현지화
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.forecast_day_header, pair.dateLabel, pair.representativeTime),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ForecastWeatherCard(
                modifier = Modifier.weight(1f), flag = depFlag, cityName = depCityName, temp = depTemp,
                weatherDesc = depOutfit.emoji + " " + (depItem.weather.firstOrNull()?.description ?: ""),
                outfit = depOutfit, feelsLike = depItem.main.feelsLike.roundToInt(),
                humidity = depItem.main.humidity,
                tempMin = depTempMin, tempMax = depTempMax,
                tempColor = depColor, cardColor = depCard, onCardColor = depOnCard
            )
            ForecastWeatherCard(
                modifier = Modifier.weight(1f), flag = dstFlag, cityName = dstCityName, temp = dstTemp,
                weatherDesc = dstOutfit.emoji + " " + (dstItem.weather.firstOrNull()?.description ?: ""),
                outfit = dstOutfit, feelsLike = dstItem.main.feelsLike.roundToInt(),
                humidity = dstItem.main.humidity,
                tempMin = dstTempMin, tempMax = dstTempMax,
                tempColor = dstColor, cardColor = dstCard, onCardColor = dstOnCard
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.dashboard_temp_gap), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(gapLabel, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.tertiary)
            }
        }

        if (depOutfit.stage != dstOutfit.stage) {
            OutfitTransitionCard(depOutfit, dstOutfit, depFlag, dstFlag)
        }
        ChecklistButton(isKrToJp, onNavigateToChecklist)
    }
}

// 공통 컴포넌트
@Composable
private fun TravelDirectionBar(direction: TravelDirection, onToggle: () -> Unit) {
    val label = if (direction == TravelDirection.KR_TO_JP)
        stringResource(R.string.direction_kr_to_jp)
    else
        stringResource(R.string.direction_jp_to_kr)

    Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = stringResource(R.string.direction_switch_label),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun TempGapCard(gapLabel: String, comparisonMessage: String, travelAdvice: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.dashboard_temp_gap), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(text = gapLabel, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comparisonMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = travelAdvice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
private fun ContextCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun ChecklistButton(isKrToJp: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(if (isKrToJp) R.string.checklist_title_japan else R.string.checklist_title_korea))
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.dashboard_loading), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRefresh: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("⚠️", style = MaterialTheme.typography.displaySmall)
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Button(onClick = onRefresh) { Text(stringResource(R.string.dashboard_retry)) }
        }
    }
}

@Composable
private fun WeatherCard(
    modifier: Modifier = Modifier,
    flag: String, cityName: String, temp: Int, weatherDesc: String,
    outfit: OutfitRecommendation, feelsLike: Int, humidity: Int,
    tempColor: androidx.compose.ui.graphics.Color,
    cardColor: androidx.compose.ui.graphics.Color,
    onCardColor: androidx.compose.ui.graphics.Color
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("$flag $cityName", style = MaterialTheme.typography.labelLarge, color = onCardColor)
            Text("$temp°", style = MaterialTheme.typography.displayMedium, color = tempColor)
            Text(weatherDesc, style = MaterialTheme.typography.labelSmall, color = onCardColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = onCardColor.copy(alpha = 0.2f))
            Text(outfit.localizedMainOutfit(), style = MaterialTheme.typography.bodySmall, color = onCardColor)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.dashboard_feels_like, feelsLike), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
                Text(stringResource(R.string.dashboard_humidity, humidity), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun ForecastWeatherCard(
    modifier: Modifier = Modifier,
    flag: String, cityName: String, temp: Int,
    weatherDesc: String, outfit: OutfitRecommendation,
    feelsLike: Int, humidity: Int,
    tempMin: Int, tempMax: Int,
    tempColor: androidx.compose.ui.graphics.Color,
    cardColor: androidx.compose.ui.graphics.Color,
    onCardColor: androidx.compose.ui.graphics.Color
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("$flag $cityName", style = MaterialTheme.typography.labelLarge, color = onCardColor)
            Text("$temp°", style = MaterialTheme.typography.displayMedium, color = tempColor)
            // 최저/최고 표시
            Text(
                text = stringResource(R.string.forecast_temp_range, tempMin, tempMax),
                style = MaterialTheme.typography.labelSmall,
                color = onCardColor.copy(alpha = 0.8f)
            )
            Text(weatherDesc, style = MaterialTheme.typography.labelSmall, color = onCardColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = onCardColor.copy(alpha = 0.2f))
            Text(outfit.localizedMainOutfit(), style = MaterialTheme.typography.bodySmall, color = onCardColor)
            Text(stringResource(R.string.dashboard_humidity, humidity), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun OutfitTransitionCard(
    departureOutfit: OutfitRecommendation, destinationOutfit: OutfitRecommendation,
    departureFlag: String, destinationFlag: String
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(departureFlag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(departureOutfit.emoji, style = MaterialTheme.typography.headlineSmall)
                Text(departureOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Text("→", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(destinationFlag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(destinationOutfit.emoji, style = MaterialTheme.typography.headlineSmall)
                Text(destinationOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

// 문자열 빌더
@Composable
private fun buildComparisonMessage(result: TempComparisonResult, isKrToJp: Boolean): String {
    val destination = localizedCityName(if (isKrToJp) result.jpCityName else result.krCityName)
    val departure   = localizedCityName(if (isKrToJp) result.krCityName else result.jpCityName)
    val gap = abs(result.gapDegree)
    return if (isKrToJp) {
        when {
            result.gapDegree > 0 -> stringResource(R.string.comparison_jp_warmer, destination, departure, gap)
            result.gapDegree < 0 -> stringResource(R.string.comparison_jp_colder, destination, departure, gap)
            else -> stringResource(R.string.comparison_same_kr_jp, destination, departure)
        }
    } else {
        when {
            result.gapDegree < 0 -> stringResource(R.string.comparison_kr_warmer, destination, departure, gap)
            result.gapDegree > 0 -> stringResource(R.string.comparison_kr_colder, destination, departure, gap)
            else -> stringResource(R.string.comparison_same_jp_kr, destination, departure)
        }
    }
}

@Composable
private fun buildTravelAdvice(result: TempComparisonResult, isKrToJp: Boolean): String {
    val directedGap = if (isKrToJp) result.gapDegree else -result.gapDegree
    val destTemp    = if (isKrToJp) result.jpTemp else result.krTemp
    return when (result.outfitGapLevel) {
        OutfitGapLevel.SIMILAR -> when {
            destTemp <= 0 -> stringResource(R.string.advice_similar_freezing)
            destTemp <= 10 -> stringResource(R.string.advice_similar_cold)
            destTemp <= 20 -> stringResource(R.string.advice_similar_mild)
            else -> stringResource(R.string.advice_similar_hot)
        }
        OutfitGapLevel.MODERATE -> if (directedGap > 0) stringResource(R.string.advice_moderate_warmer) else stringResource(R.string.advice_moderate_colder)
        OutfitGapLevel.SIGNIFICANT -> if (directedGap > 0) stringResource(R.string.advice_significant_warmer) else stringResource(R.string.advice_significant_colder)
    }
}

@Composable
private fun buildContextMessage(result: ContextAwareResult, actualTemp: Double): String {
    val tempDiff = actualTemp - result.feelsLikeTemp
    return when {
        result.windLevel == WindLevel.VERY_WINDY && tempDiff >= 5 -> stringResource(R.string.context_very_windy_cold)
        result.windLevel == WindLevel.WINDY && tempDiff >= 3 -> stringResource(R.string.context_windy_cold)
        result.humidityLevel == HumidityLevel.VERY_HUMID && result.feelsLikeTemp > actualTemp -> stringResource(R.string.context_very_humid_hot)
        result.humidityLevel == HumidityLevel.HUMID -> stringResource(R.string.context_humid)
        result.humidityLevel == HumidityLevel.DRY -> stringResource(R.string.context_dry)
        else -> stringResource(R.string.context_comfortable)
    }
}

@Composable
fun OutfitRecommendation.localizedMainOutfit(): String = stringResource(
    when (stage) {
        1 -> R.string.outfit_stage1
        2 -> R.string.outfit_stage2
        3 -> R.string.outfit_stage3
        4 -> R.string.outfit_stage4
        5 -> R.string.outfit_stage5
        6 -> R.string.outfit_stage6
        7 -> R.string.outfit_stage7
        else -> R.string.outfit_stage8
    }
)