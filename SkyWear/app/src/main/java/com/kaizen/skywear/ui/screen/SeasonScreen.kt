package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.R
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.domain.JP_SEASON_EVENTS
import com.kaizen.skywear.domain.KR_SEASON_EVENTS
import com.kaizen.skywear.domain.SeasonEvent
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val travelDirection by viewModel.travelDirection.collectAsState()
    val isKrToJp = travelDirection == TravelDirection.KR_TO_JP
    val events   = if (isKrToJp) JP_SEASON_EVENTS else KR_SEASON_EVENTS
    val sorted   = events.sortedBy { it.dDay() }
    val now      = sorted.filter { it.isNow() }
    val upcoming = sorted.filter { !it.isNow() }
    val lang     = Locale.getDefault().language

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(title = { Text(stringResource(if (isKrToJp) R.string.season_title_jp else R.string.season_title_kr)) })
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(padding)
        ) {
            if (now.isNotEmpty()) {
                item {
                    Text(stringResource(R.string.season_now_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 2.dp))
                }
                items(now) { event -> SeasonCard(event = event, lang = lang, isNow = true) }
                item { Spacer(Modifier.height(4.dp)) }
            }
            item {
                Text(stringResource(R.string.season_upcoming_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))
            }
            items(upcoming) { event -> SeasonCard(event = event, lang = lang, isNow = false) }
        }
    }
}

@Composable
private fun SeasonCard(event: SeasonEvent, lang: String, isNow: Boolean) {
    val title   = when (lang) { "en" -> event.titleEn; "ja" -> event.titleJa; else -> event.titleKo }
    val avgTemp = when (lang) { "en" -> event.avgTempEn; "ja" -> event.avgTempJa; else -> event.avgTempKo }
    val tip     = when (lang) { "en" -> event.tipEn; "ja" -> event.tipJa; else -> event.tipKo }
    val dDay    = event.dDay()
    val dDayText = if (dDay == 0L) stringResource(R.string.season_dday_today) else stringResource(R.string.season_dday, dDay)

    val accentColor = when (event.colorTag) { "green" -> Color(0xFF639922); "blue" -> Color(0xFF378ADD); "amber" -> Color(0xFFEF9F27); else -> Color(0xFFD85A30) }
    val bgColor     = when (event.colorTag) { "green" -> Color(0xFFEAF3DE); "blue" -> Color(0xFFE6F1FB); "amber" -> Color(0xFFFAEEDA); else -> Color(0xFFFAECE7) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isNow) bgColor else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(event.emoji, style = MaterialTheme.typography.titleMedium)
                    Column {
                        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(event.city, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(color = if (isNow) accentColor else MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = if (isNow) stringResource(R.string.season_best_now) else dDayText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNow) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(avgTemp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Surface(color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp)) {
                Text(tip, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))
            }
        }
    }
}