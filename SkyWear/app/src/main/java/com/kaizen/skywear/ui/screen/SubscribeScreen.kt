package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.R
import com.kaizen.skywear.data.local.SubscribedCity
import com.kaizen.skywear.data.model.City
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.ui.viewmodel.SubscribeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribeScreen(viewModel: SubscribeViewModel = hiltViewModel()) {
    val cities     by viewModel.subscribedCities.collectAsState()
    val query      by viewModel.searchQuery.collectAsState()
    val results    by viewModel.searchResults.collectAsState()
    val weatherMap by viewModel.weatherMap.collectAsState()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_subscribe)) },
                actions = {
                    IconButton(onClick = { viewModel.refreshAllWeather() }) { Icon(Icons.Default.Refresh, null) }
                    IconButton(onClick = { showSearch = !showSearch }) { Icon(Icons.Default.Add, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showSearch) {
                OutlinedTextField(
                    value = query, onValueChange = { viewModel.searchCity(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                    placeholder = { Text(stringResource(R.string.subscribe_add_hint), style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
                if (results.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 180.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(results) { city ->
                            SearchResultItem(city = city, onAdd = { viewModel.addCity(city); showSearch = false })
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            if (cities.size >= 5) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(stringResource(R.string.subscribe_max_reached), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(8.dp))
                }
            }

            if (cities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🔔", style = MaterialTheme.typography.displaySmall)
                        Text(stringResource(R.string.subscribe_empty_title), style = MaterialTheme.typography.titleSmall)
                        Text(stringResource(R.string.subscribe_empty_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cities, key = { it.nameEn }) { city ->
                        val weather = weatherMap[city.nameEn]
                        SubscribedCityCard(city = city, temp = weather?.first, weatherDesc = weather?.second, onToggleAlert = { viewModel.toggleAlert(city) }, onDelete = { viewModel.removeCity(city) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscribedCityCard(city: SubscribedCity, temp: Int?, weatherDesc: String?, onToggleAlert: () -> Unit, onDelete: () -> Unit) {
    val alertLabel = stringResource(if (city.isAlertOn) R.string.subscribe_alert_on else R.string.subscribe_alert_off)
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(city.emoji, style = MaterialTheme.typography.titleMedium)
                Column {
                    Text(localizedCityName(city.nameEn), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(buildString { if (weatherDesc != null) append("$weatherDesc · "); append(alertLabel) }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (temp != null) Text("$temp°", style = MaterialTheme.typography.titleMedium, color = if (city.country == "KR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                IconButton(onClick = onToggleAlert, modifier = Modifier.size(32.dp)) {
                    Icon(if (city.isAlertOn) Icons.Default.Notifications else Icons.Default.NotificationsOff, null, tint = if (city.isAlertOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(city: City, onAdd: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(city.emoji, style = MaterialTheme.typography.titleSmall)
                Column {
                    Text(localizedCityName(city.nameEn), style = MaterialTheme.typography.bodySmall)
                    Text(city.nameEn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextButton(onClick = onAdd, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text(stringResource(R.string.subscribe_add_button), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}