package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.R
import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistItem
import com.kaizen.skywear.ui.viewmodel.ChecklistViewModel

// 일본 여행 체크리스트 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onBack: () -> Unit,
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    val allItems by viewModel.allItems.collectAsState()
    val checkedCount by viewModel.checkedCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // 카테고리 필터링
    val displayItems = if (selectedCategory == null) {
        allItems
    } else {
        allItems.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checklist_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.deleteCheckedItems() }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.checklist_delete_checked))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.checklist_add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 진행률 바
            if (totalCount > 0) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.checklist_progress),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$checkedCount / $totalCount",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { viewModel.getProgress(checkedCount, totalCount) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(0.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            // 카테고리 필터
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.filterByCategory(null) },
                        label = { Text(stringResource(R.string.checklist_all)) }
                    )
                }
                items(ChecklistCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.filterByCategory(category) },
                        label = { Text(category.toLocalizedString()) }
                    )
                }
            }

            // 체크리스트
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayItems, key = { it.id }) { item ->
                    ChecklistItemCard(
                        item = item,
                        onToggle = { viewModel.toggleCheck(item) },
                        onDelete = {
                            if (!item.isDefault) viewModel.deleteItem(item)
                        }
                    )
                }
            }
        }

        // 항목 추가 다이얼로그
        if (showAddDialog) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, category ->
                    viewModel.addItem(title, category)
                    showAddDialog = false
                }
            )
        }
    }
}

// 체크리스트 아이템 카드
@Composable
private fun ChecklistItemCard(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggle() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isChecked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                )
                Text(
                    text = item.category.toLocalizedString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!item.isDefault) {
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// 항목 추가 다이얼로그
@Composable
private fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, ChecklistCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ChecklistCategory.MISC) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checklist_add_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.checklist_add_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.checklist_add_category), style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ChecklistCategory.entries) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.toLocalizedString()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title, selectedCategory) },
                enabled = title.isNotBlank()
            ) { Text(stringResource(R.string.checklist_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.checklist_cancel)) }
        }
    )
}

// stringResource 를 Composable 밖에서 쓸 수 없어서 enum 확장함수로 분리
@Composable
fun ChecklistCategory.toLocalizedString(): String = stringResource(
    when (this) {
        ChecklistCategory.DOCUMENT   -> R.string.category_document
        ChecklistCategory.MONEY      -> R.string.category_money
        ChecklistCategory.ELECTRONIC -> R.string.category_electronic
        ChecklistCategory.CLOTHING   -> R.string.category_clothing
        ChecklistCategory.HEALTH     -> R.string.category_health
        ChecklistCategory.MISC       -> R.string.category_misc
    }
)
