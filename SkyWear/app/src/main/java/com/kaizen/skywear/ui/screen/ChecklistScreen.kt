package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                    text = item.category.toKorean(),
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
        title = { Text("항목 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("항목 이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("카테고리", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ChecklistCategory.entries) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.toKorean()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title, selectedCategory) },
                enabled = title.isNotBlank()
            ) { Text("추가") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

// 카테고리 한국어 변환
fun ChecklistCategory.toKorean(): String = when(this) {
    ChecklistCategory.DOCUMENT -> "📄 서류"
    ChecklistCategory.MONEY -> "💴 금융"
    ChecklistCategory.ELECTRONIC -> "🔌 전자기기"
    ChecklistCategory.CLOTHING -> "👕 의류"
    ChecklistCategory.HEALTH -> "💊 건강"
    ChecklistCategory.MISC -> "📦 기타"
}
