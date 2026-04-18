package com.kaizen.skywear.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.skywear.data.local.ChecklistCategory
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

// 항목 추가 다이얼로그

// 카테고리 한국어 변환
fun ChecklistCategory.toKorean(): String = when(this) {
    ChecklistCategory.DOCUMENT -> "📄 서류"
    ChecklistCategory.MONEY -> "💴 금융"
    ChecklistCategory.ELECTRONIC -> "🔌 전자기기"
    ChecklistCategory.CLOTHING -> "👕 의류"
    ChecklistCategory.HEALTH -> "💊 건강"
    ChecklistCategory.MISC -> "📦 기타"
}
