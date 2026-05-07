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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    val journals by viewModel.journals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_journal)) }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        if (journals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📖", style = MaterialTheme.typography.displaySmall)
                    Text(stringResource(R.string.journal_empty_title), style = MaterialTheme.typography.titleSmall)
                    Text(stringResource(R.string.journal_empty_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(journals, key = { it.id }) { journal ->
                    JournalCard(journal = journal, onDelete = { viewModel.deleteJournal(journal) })
                }
            }
        }
        if (showAddDialog) {
            AddJournalDialog(onDismiss = { showAddDialog = false }, onConfirm = { journal -> viewModel.addJournal(journal); showAddDialog = false })
        }
    }
}

@Composable
private fun JournalCard(journal: TravelJournal, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(journal.destinationEmoji, style = MaterialTheme.typography.titleMedium)
                    Column {
                        Text(journal.destination, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("${journal.startDate} ~ ${journal.endDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WeatherChip(Modifier.weight(1f), stringResource(R.string.journal_depart_label), journal.departTemp, journal.departWeatherDesc, journal.departOutfit, true)
                Text("→", modifier = Modifier.align(Alignment.CenterVertically), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                WeatherChip(Modifier.weight(1f), stringResource(R.string.journal_return_label), journal.returnTemp, journal.returnWeatherDesc, journal.returnOutfit, false)
            }
            if (journal.memo.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)) {
                    Text(journal.memo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WeatherChip(modifier: Modifier = Modifier, label: String, temp: Int, desc: String, outfit: String, isBlue: Boolean) {
    val bg = if (isBlue) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val on = if (isBlue) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Surface(color = bg, shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = on)
            Text("$temp°", style = MaterialTheme.typography.titleMedium, color = on)
            if (desc.isNotBlank()) Text(desc, style = MaterialTheme.typography.labelSmall, color = on)
            if (outfit.isNotBlank()) Text(outfit, style = MaterialTheme.typography.labelSmall, color = on)
        }
    }
}

@Composable
private fun AddJournalDialog(onDismiss: () -> Unit, onConfirm: (TravelJournal) -> Unit) {
    var destination  by remember { mutableStateOf("") }
    var startDate    by remember { mutableStateOf("") }
    var endDate      by remember { mutableStateOf("") }
    var departTemp   by remember { mutableStateOf("") }
    var departDesc   by remember { mutableStateOf("") }
    var departOutfit by remember { mutableStateOf("") }
    var returnTemp   by remember { mutableStateOf("") }
    var returnDesc   by remember { mutableStateOf("") }
    var returnOutfit by remember { mutableStateOf("") }
    var memo         by remember { mutableStateOf("") }
    var isJapan      by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.journal_add_title), style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = isJapan, onClick = { isJapan = true }, label = { Text(stringResource(R.string.journal_tab_japan), style = MaterialTheme.typography.labelSmall) })
                    FilterChip(selected = !isJapan, onClick = { isJapan = false }, label = { Text(stringResource(R.string.journal_tab_korea), style = MaterialTheme.typography.labelSmall) })
                }
                OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text(stringResource(R.string.journal_destination), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.fillMaxWidth(), textStyle = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text(stringResource(R.string.journal_start_date), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text(stringResource(R.string.journal_end_date), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                }
                Text(stringResource(R.string.journal_depart_weather), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = departTemp, onValueChange = { departTemp = it }, label = { Text(stringResource(R.string.journal_temp), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = departOutfit, onValueChange = { departOutfit = it }, label = { Text(stringResource(R.string.journal_outfit), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                }
                Text(stringResource(R.string.journal_return_weather), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = returnTemp, onValueChange = { returnTemp = it }, label = { Text(stringResource(R.string.journal_temp), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = returnOutfit, onValueChange = { returnOutfit = it }, label = { Text(stringResource(R.string.journal_outfit), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(value = memo, onValueChange = { memo = it }, label = { Text(stringResource(R.string.journal_memo), style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.fillMaxWidth(), maxLines = 3, textStyle = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = destination.isNotBlank() && startDate.isNotBlank(), onClick = {
                onConfirm(TravelJournal(destination = destination, destinationEmoji = if (isJapan) "🇯🇵" else "🇰🇷", startDate = startDate, endDate = endDate, departTemp = departTemp.toIntOrNull() ?: 0, departWeatherDesc = departDesc, departOutfit = departOutfit, returnTemp = returnTemp.toIntOrNull() ?: 0, returnWeatherDesc = returnDesc, returnOutfit = returnOutfit, memo = memo))
            }) { Text(stringResource(R.string.journal_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.journal_cancel)) } }
    )
}