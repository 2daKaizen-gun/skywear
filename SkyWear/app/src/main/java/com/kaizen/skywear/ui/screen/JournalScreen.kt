package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.R
import com.kaizen.skywear.data.local.TravelJournal
import com.kaizen.skywear.ui.viewmodel.JournalViewModel

// 여행 일지 + 날씨 기록
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val journals by viewModel.journals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_journal)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        if (journals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📖", style = MaterialTheme.typography.displayMedium)
                    Text("첫 여행 일지를 기록해보세요", style = MaterialTheme.typography.titleMedium)
                    Text("여행의 날씨와 코디를 기억할 수 있어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(journals, key = { it.id }) { journal ->
                    JournalCard(
                        journal = journal,
                        onDelete = { viewModel.deleteJournal(journal) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddJournalDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { journal ->
                    viewModel.addJournal(journal)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun JournalCard(journal: TravelJournal, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(journal.destinationEmoji, style = MaterialTheme.typography.titleLarge)
                    Column {
                        Text(journal.destination, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text("${journal.startDate} ~ ${journal.endDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null,
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            // 출발/귀국 날씨 비교
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WeatherChip(
                    modifier = Modifier.weight(1f),
                    label = "출발",
                    temp = journal.departTemp,
                    desc = journal.departWeatherDesc,
                    outfit = journal.departOutfit,
                    isBlue = true
                )
                Text("→", modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                WeatherChip(
                    modifier = Modifier.weight(1f),
                    label = "귀국",
                    temp = journal.returnTemp,
                    desc = journal.returnWeatherDesc,
                    outfit = journal.returnOutfit,
                    isBlue = false
                )
            }

            if (journal.memo.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(journal.memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WeatherChip(
    modifier: Modifier = Modifier,
    label: String, temp: Int, desc: String, outfit: String, isBlue: Boolean
) {
    val bg = if (isBlue) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val on = if (isBlue) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Surface(color = bg, shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = on)
            Text("$temp°", style = MaterialTheme.typography.titleLarge, color = on)
            if (desc.isNotBlank()) Text(desc, style = MaterialTheme.typography.labelSmall, color = on)
            if (outfit.isNotBlank()) Text(outfit, style = MaterialTheme.typography.labelSmall, color = on)
        }
    }
}

// 일지 추가 다이얼로그
@Composable
private fun AddJournalDialog(onDismiss: () -> Unit, onConfirm: (TravelJournal) -> Unit) {
    var destination     by remember { mutableStateOf("") }
    var startDate       by remember { mutableStateOf("") }
    var endDate         by remember { mutableStateOf("") }
    var departTemp      by remember { mutableStateOf("") }
    var departDesc      by remember { mutableStateOf("") }
    var departOutfit    by remember { mutableStateOf("") }
    var returnTemp      by remember { mutableStateOf("") }
    var returnDesc      by remember { mutableStateOf("") }
    var returnOutfit    by remember { mutableStateOf("") }
    var memo            by remember { mutableStateOf("") }
    var isJapan         by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("여행 일지 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = isJapan, onClick = { isJapan = true }, label = { Text("🇯🇵 일본") })
                    FilterChip(selected = !isJapan, onClick = { isJapan = false }, label = { Text("🇰🇷 한국") })
                }
                OutlinedTextField(value = destination, onValueChange = { destination = it },
                    label = { Text("목적지 도시") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it },
                        label = { Text("출발일 (MM-DD)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it },
                        label = { Text("귀국일") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Text("출발일 날씨", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = departTemp, onValueChange = { departTemp = it },
                        label = { Text("기온 (°C)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = departOutfit, onValueChange = { departOutfit = it },
                        label = { Text("코디") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                Text("귀국일 날씨", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = returnTemp, onValueChange = { returnTemp = it },
                        label = { Text("기온 (°C)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = returnOutfit, onValueChange = { returnOutfit = it },
                        label = { Text("코디") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = memo, onValueChange = { memo = it },
                    label = { Text("메모") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            }
        },
        confirmButton = {
            TextButton(
                enabled = destination.isNotBlank() && startDate.isNotBlank(),
                onClick = {
                    onConfirm(TravelJournal(
                        destination = destination,
                        destinationEmoji = if (isJapan) "🇯🇵" else "🇰🇷",
                        startDate = startDate, endDate = endDate,
                        departTemp = departTemp.toIntOrNull() ?: 0,
                        departWeatherDesc = departDesc, departOutfit = departOutfit,
                        returnTemp = returnTemp.toIntOrNull() ?: 0,
                        returnWeatherDesc = returnDesc, returnOutfit = returnOutfit,
                        memo = memo
                    ))
                }
            ) { Text("저장") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}