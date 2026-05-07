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
    val selectedKrCity  by viewModel.selectedKrCity.collectAsState()
    val selectedJpCity  by viewModel.selectedJpCity.collectAsState()
    val colors = LocalExtraColors.current
    val isKrToJp = travelDirection == TravelDirection.KR_TO_JP

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.titleLarge) },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TravelDirectionBar(direction = travelDirection, onToggle = { viewModel.toggleTravelDirection() })
            DateSelectorRow(forecastState = forecastState, selectedDateKey = selectedDateKey, onDateSelected = { viewModel.selectDate(it) })

            if (selectedDateKey == null) {
                CurrentWeatherContent(uiState = uiState, isKrToJp = isKrToJp, colors = colors, onNavigateToChecklist = onNavigateToChecklist, onRefresh = { viewModel.refresh() })
            } else {
                val selectedPair = (forecastState as? ForecastUiState.Success)?.pairs?.firstOrNull { it.dateKey == selectedDateKey }
                if (selectedPair != null) {
                    ForecastDayContent(pair = selectedPair, isKrToJp = isKrToJp, colors = colors, krCityName = selectedKrCity, jpCityName = selectedJpCity, onNavigateToChecklist = onNavigateToChecklist)
                }
            }
        }
    }
}

@Composable
private fun DateSelectorRow(forecastState: ForecastUiState, selectedDateKey: String?, onDateSelected: (String?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(selected = selectedDateKey == null, onClick = { onDateSelected(null) }, label = { Text(stringResource(R.string.forecast_now), style = MaterialTheme.typography.labelSmall) })
        if (forecastState is ForecastUiState.Success) {
            forecastState.pairs.forEach { pair ->
                FilterChip(
                    selected = selectedDateKey == pair.dateKey,
                    onClick = { onDateSelected(pair.dateKey) },
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(pair.dateLabel, style = MaterialTheme.typography.labelSmall)
                            Text(pair.representativeTime, style = MaterialTheme.typography.labelSmall.copy(fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrentWeatherContent(uiState: WeatherUiState, isKrToJp: Boolean, colors: com.kaizen.skywear.ui.theme.SkyWearExtraColors, onNavigateToChecklist: () -> Unit, onRefresh: () -> Unit) {
    when (uiState) {
        is WeatherUiState.Loading -> LoadingContent()
        is WeatherUiState.Success -> {
            val depWeather  = if (isKrToJp) uiState.krWeather else uiState.jpWeather
            val dstWeather  = if (isKrToJp) uiState.jpWeather else uiState.krWeather
            val depContext  = if (isKrToJp) uiState.krContextResult else uiState.jpContextResult
            val dstContext  = if (isKrToJp) uiState.jpContextResult else uiState.krContextResult
            val depColor    = if (isKrToJp) colors.koreaRed else colors.japanBlue
            val dstColor    = if (isKrToJp) colors.japanBlue else colors.koreaRed
            val depFlag     = if (isKrToJp) "🇰🇷" else "🇯🇵"
            val dstFlag     = if (isKrToJp) "🇯🇵" else "🇰🇷"
            val depCard     = if (isKrToJp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            val dstCard     = if (isKrToJp) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
            val depOnCard   = if (isKrToJp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            val dstOnCard   = if (isKrToJp) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

            val comparisonMessage = buildComparisonMessage(uiState.comparisonResult, isKrToJp)
            val travelAdvice      = buildTravelAdvice(uiState.comparisonResult, isKrToJp)
            val contextMessage    = buildContextMessage(depContext, depWeather.main.temp)

            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WeatherCard(Modifier.weight(1f), depFlag, localizedCityName(depWeather.cityName), depWeather.tempRounded(),
                        depContext.adjustedOutfit.emoji + " " + (depWeather.weather.firstOrNull()?.description ?: ""),
                        depContext.adjustedOutfit, depWeather.main.feelsLike.roundToInt(), depWeather.main.humidity, depColor, depCard, depOnCard)
                    WeatherCard(Modifier.weight(1f), dstFlag, localizedCityName(dstWeather.cityName), dstWeather.tempRounded(),
                        dstContext.adjustedOutfit.emoji + " " + (dstWeather.weather.firstOrNull()?.description ?: ""),
                        dstContext.adjustedOutfit, dstWeather.main.feelsLike.roundToInt(), dstWeather.main.humidity, dstColor, dstCard, dstOnCard)
                }
                TempGapCard(uiState.comparisonResult.directedGapLabel(isKrToJp), comparisonMessage, travelAdvice)
                if (kotlin.math.abs(depWeather.main.feelsLike - depWeather.main.temp) >= 2.0) ContextCard(contextMessage)
                if (uiState.comparisonResult.isOutfitDifferent()) {
                    OutfitTransitionCard(
                        if (isKrToJp) uiState.comparisonResult.krOutfit else uiState.comparisonResult.jpOutfit,
                        if (isKrToJp) uiState.comparisonResult.jpOutfit else uiState.comparisonResult.krOutfit,
                        depFlag, dstFlag
                    )
                }
                ChecklistButton(isKrToJp, onNavigateToChecklist)
            }
        }
        is WeatherUiState.Error -> ErrorContent(uiState.message, onRefresh)
    }
}

@Composable
private fun ForecastDayContent(pair: DailyForecastPair, isKrToJp: Boolean, colors: com.kaizen.skywear.ui.theme.SkyWearExtraColors, krCityName: String, jpCityName: String, onNavigateToChecklist: () -> Unit) {
    val depItem    = if (isKrToJp) pair.krItem else pair.jpItem
    val dstItem    = if (isKrToJp) pair.jpItem else pair.krItem
    val depColor   = if (isKrToJp) colors.koreaRed else colors.japanBlue
    val dstColor   = if (isKrToJp) colors.japanBlue else colors.koreaRed
    val depFlag    = if (isKrToJp) "🇰🇷" else "🇯🇵"
    val dstFlag    = if (isKrToJp) "🇯🇵" else "🇰🇷"
    val depCard    = if (isKrToJp) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val dstCard    = if (isKrToJp) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
    val depOnCard  = if (isKrToJp) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val dstOnCard  = if (isKrToJp) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    val depTempMin = if (isKrToJp) pair.krTempMin else pair.jpTempMin
    val depTempMax = if (isKrToJp) pair.krTempMax else pair.jpTempMax
    val dstTempMin = if (isKrToJp) pair.jpTempMin else pair.krTempMin
    val dstTempMax = if (isKrToJp) pair.jpTempMax else pair.krTempMax

    val depOutfit  = getOutfitRecommendation(depItem.main.temp)
    val dstOutfit  = getOutfitRecommendation(dstItem.main.temp)
    val depTemp    = depItem.main.temp.roundToInt()
    val dstTemp    = dstItem.main.temp.roundToInt()
    val gapDegree  = dstTemp - depTemp
    val gapLabel   = if (gapDegree >= 0) "+${gapDegree}°C" else "${gapDegree}°C"

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
            Text(stringResource(R.string.forecast_day_header, pair.dateLabel, pair.representativeTime),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForecastWeatherCard(Modifier.weight(1f), depFlag,
                localizedCityName(if (isKrToJp) krCityName else jpCityName), depTemp,
                depOutfit.emoji + " " + (depItem.weather.firstOrNull()?.description ?: ""),
                depOutfit, depItem.main.feelsLike.roundToInt(), depItem.main.humidity, depTempMin, depTempMax, depColor, depCard, depOnCard)
            ForecastWeatherCard(Modifier.weight(1f), dstFlag,
                localizedCityName(if (isKrToJp) jpCityName else krCityName), dstTemp,
                dstOutfit.emoji + " " + (dstItem.weather.firstOrNull()?.description ?: ""),
                dstOutfit, dstItem.main.feelsLike.roundToInt(), dstItem.main.humidity, dstTempMin, dstTempMax, dstColor, dstCard, dstOnCard)
        }
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = RoundedCornerShape(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.dashboard_temp_gap), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(gapLabel, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        if (depOutfit.stage != dstOutfit.stage) OutfitTransitionCard(depOutfit, dstOutfit, depFlag, dstFlag)
        ChecklistButton(isKrToJp, onNavigateToChecklist)
    }
}

// 공통 컴포넌트

@Composable
private fun TravelDirectionBar(direction: TravelDirection, onToggle: () -> Unit) {
    val label = stringResource(if (direction == TravelDirection.KR_TO_JP) R.string.direction_kr_to_jp else R.string.direction_jp_to_kr)
    Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            IconButton(onClick = onToggle, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = stringResource(R.string.direction_switch_label), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun TempGapCard(gapLabel: String, comparisonMessage: String, travelAdvice: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.dashboard_temp_gap), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text(gapLabel, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.height(6.dp))
            Text(comparisonMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(Modifier.height(2.dp))
            Text(travelAdvice, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
private fun ContextCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
        Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun ChecklistButton(isKrToJp: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(stringResource(if (isKrToJp) R.string.checklist_title_japan else R.string.checklist_title_korea), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.dashboard_loading), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRefresh: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("⚠️", style = MaterialTheme.typography.displaySmall)
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Button(onClick = onRefresh) { Text(stringResource(R.string.dashboard_retry)) }
        }
    }
}

@Composable
private fun WeatherCard(modifier: Modifier = Modifier, flag: String, cityName: String, temp: Int, weatherDesc: String, outfit: OutfitRecommendation, feelsLike: Int, humidity: Int, tempColor: androidx.compose.ui.graphics.Color, cardColor: androidx.compose.ui.graphics.Color, onCardColor: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("$flag $cityName", style = MaterialTheme.typography.labelMedium, color = onCardColor)
            Text("$temp°", style = MaterialTheme.typography.displaySmall, color = tempColor)
            Text(weatherDesc, style = MaterialTheme.typography.labelSmall, color = onCardColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp), color = onCardColor.copy(alpha = 0.2f))
            Text(outfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = onCardColor)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.dashboard_feels_like, feelsLike), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
                Text(stringResource(R.string.dashboard_humidity, humidity), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun ForecastWeatherCard(modifier: Modifier = Modifier, flag: String, cityName: String, temp: Int, weatherDesc: String, outfit: OutfitRecommendation, feelsLike: Int, humidity: Int, tempMin: Int, tempMax: Int, tempColor: androidx.compose.ui.graphics.Color, cardColor: androidx.compose.ui.graphics.Color, onCardColor: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("$flag $cityName", style = MaterialTheme.typography.labelMedium, color = onCardColor)
            Text("$temp°", style = MaterialTheme.typography.displaySmall, color = tempColor)
            Text(stringResource(R.string.forecast_temp_range, tempMin, tempMax), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.8f))
            Text(weatherDesc, style = MaterialTheme.typography.labelSmall, color = onCardColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp), color = onCardColor.copy(alpha = 0.2f))
            Text(outfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = onCardColor)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.dashboard_feels_like, feelsLike), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
                Text(stringResource(R.string.dashboard_humidity, humidity), style = MaterialTheme.typography.labelSmall, color = onCardColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun OutfitTransitionCard(depOutfit: OutfitRecommendation, dstOutfit: OutfitRecommendation, depFlag: String, dstFlag: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(depFlag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(depOutfit.emoji, style = MaterialTheme.typography.titleLarge)
                Text(depOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Text("→", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(dstFlag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dstOutfit.emoji, style = MaterialTheme.typography.titleLarge)
                Text(dstOutfit.localizedMainOutfit(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

// 문자열 빌더

@Composable
private fun buildComparisonMessage(result: TempComparisonResult, isKrToJp: Boolean): String {
    val dst = localizedCityName(if (isKrToJp) result.jpCityName else result.krCityName)
    val dep = localizedCityName(if (isKrToJp) result.krCityName else result.jpCityName)
    val gap = abs(result.gapDegree)
    return if (isKrToJp) {
        when {
            result.gapDegree > 0 -> stringResource(R.string.comparison_jp_warmer, dst, dep, gap)
            result.gapDegree < 0 -> stringResource(R.string.comparison_jp_colder, dst, dep, gap)
            else -> stringResource(R.string.comparison_same_kr_jp, dst, dep)
        }
    } else {
        when {
            result.gapDegree < 0 -> stringResource(R.string.comparison_kr_warmer, dst, dep, gap)
            result.gapDegree > 0 -> stringResource(R.string.comparison_kr_colder, dst, dep, gap)
            else -> stringResource(R.string.comparison_same_jp_kr, dst, dep)
        }
    }
}

@Composable
private fun buildTravelAdvice(result: TempComparisonResult, isKrToJp: Boolean): String {
    val directedGap = if (isKrToJp) result.gapDegree else -result.gapDegree
    val destTemp    = if (isKrToJp) result.jpTemp else result.krTemp
    return when (result.outfitGapLevel) {
        OutfitGapLevel.SIMILAR -> when {
            destTemp <= 0  -> stringResource(R.string.advice_similar_freezing)
            destTemp <= 10 -> stringResource(R.string.advice_similar_cold)
            destTemp <= 20 -> stringResource(R.string.advice_similar_mild)
            else           -> stringResource(R.string.advice_similar_hot)
        }
        OutfitGapLevel.MODERATE    -> if (directedGap > 0) stringResource(R.string.advice_moderate_warmer) else stringResource(R.string.advice_moderate_colder)
        OutfitGapLevel.SIGNIFICANT -> if (directedGap > 0) stringResource(R.string.advice_significant_warmer) else stringResource(R.string.advice_significant_colder)
    }
}

@Composable
private fun buildContextMessage(result: ContextAwareResult, actualTemp: Double): String {
    val diff = actualTemp - result.feelsLikeTemp
    return when {
        result.windLevel == WindLevel.VERY_WINDY && diff >= 5 -> stringResource(R.string.context_very_windy_cold)
        result.windLevel == WindLevel.WINDY && diff >= 3      -> stringResource(R.string.context_windy_cold)
        result.humidityLevel == HumidityLevel.VERY_HUMID && result.feelsLikeTemp > actualTemp -> stringResource(R.string.context_very_humid_hot)
        result.humidityLevel == HumidityLevel.HUMID           -> stringResource(R.string.context_humid)
        result.humidityLevel == HumidityLevel.DRY             -> stringResource(R.string.context_dry)
        else                                                   -> stringResource(R.string.context_comfortable)
    }
}

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