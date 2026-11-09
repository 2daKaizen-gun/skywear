package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaizen.skywear.R
import com.kaizen.skywear.data.local.TravelJournal
import com.kaizen.skywear.ui.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    val journals by viewModel.journals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_journal)) }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.journal_add_title)) } }
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
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
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

// 탭하면 DatePicker를 여는 읽기 전용 날짜 필드
@Composable
private fun DateField(label: String, value: String, isError: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { if (it is PressInteraction.Release) onClick() }
    }
    OutlinedTextField(
        value = value, onValueChange = {}, readOnly = true, isError = isError,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = label, modifier = Modifier.size(18.dp)) },
        singleLine = true, modifier = modifier, textStyle = MaterialTheme.typography.bodySmall,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalDatePickerDialog(initialDate: String, onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val state = rememberDatePickerState(initialSelectedDateMillis = JournalInput.dateStringToMillis(initialDate))
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = { state.selectedDateMillis?.let { onDateSelected(JournalInput.millisToDateString(it)) } }
            ) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.journal_cancel)) } }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun AddJournalDialog(onDismiss: () -> Unit, onConfirm: (TravelJournal) -> Unit) {
    var destination  by remember { mutableStateOf("") }
    var startDate    by remember { mutableStateOf("") }
    var endDate      by remember { mutableStateOf("") }
    var departTemp   by remember { mutableStateOf("") }
    var departOutfit by remember { mutableStateOf("") }
    var returnTemp   by remember { mutableStateOf("") }
    var returnOutfit by remember { mutableStateOf("") }
    var memo         by remember { mutableStateOf("") }
    var isJapan      by remember { mutableStateOf(true) }
    var pickingStart by remember { mutableStateOf(false) }
    var pickingEnd   by remember { mutableStateOf(false) }

    val dateRangeError = startDate.isNotEmpty() && endDate.isNotEmpty() && !JournalInput.isValidDateRange(startDate, endDate)
    val canSave = destination.isNotBlank() && startDate.isNotEmpty() && endDate.isNotEmpty() && !dateRangeError
    val numberKeyboard = KeyboardOptions(keyboardType = KeyboardType.Number)

    if (pickingStart) {
        JournalDatePickerDialog(initialDate = startDate, onDismiss = { pickingStart = false }) { picked ->
            startDate = picked
            if (endDate.isEmpty()) endDate = picked // 귀국일 미입력 시 출발일로 기본 설정
            pickingStart = false
        }
    }
    if (pickingEnd) {
        JournalDatePickerDialog(initialDate = endDate.ifEmpty { startDate }, onDismiss = { pickingEnd = false }) { picked ->
            endDate = picked
            pickingEnd = false
        }
    }

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
                    DateField(stringResource(R.string.journal_start_date), startDate, isError = false, onClick = { pickingStart = true }, modifier = Modifier.weight(1f))
                    DateField(stringResource(R.string.journal_end_date), endDate, isError = dateRangeError, onClick = { pickingEnd = true }, modifier = Modifier.weight(1f))
                }
                if (dateRangeError) {
                    Text(stringResource(R.string.journal_date_range_error), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
                Text(stringResource(R.string.journal_depart_weather), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = departTemp, onValueChange = { if (JournalInput.isAcceptableTempInput(it)) departTemp = it }, label = { Text(stringResource(R.string.journal_temp), style = MaterialTheme.typography.labelSmall) }, singleLine = true, keyboardOptions = numberKeyboard, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = departOutfit, onValueChange = { departOutfit = it }, label = { Text(stringResource(R.string.journal_outfit), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                }
                Text(stringResource(R.string.journal_return_weather), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = returnTemp, onValueChange = { if (JournalInput.isAcceptableTempInput(it)) returnTemp = it }, label = { Text(stringResource(R.string.journal_temp), style = MaterialTheme.typography.labelSmall) }, singleLine = true, keyboardOptions = numberKeyboard, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = returnOutfit, onValueChange = { returnOutfit = it }, label = { Text(stringResource(R.string.journal_outfit), style = MaterialTheme.typography.labelSmall) }, singleLine = true, modifier = Modifier.weight(1f), textStyle = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(value = memo, onValueChange = { memo = it }, label = { Text(stringResource(R.string.journal_memo), style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.fillMaxWidth(), maxLines = 3, textStyle = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = {
                onConfirm(TravelJournal(destination = destination.trim(), destinationEmoji = if (isJapan) "🇯🇵" else "🇰🇷", startDate = startDate, endDate = endDate, departTemp = JournalInput.parseTemp(departTemp), departOutfit = departOutfit, returnTemp = JournalInput.parseTemp(returnTemp), returnOutfit = returnOutfit, memo = memo))
            }) { Text(stringResource(R.string.journal_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.journal_cancel)) } }
    )
}