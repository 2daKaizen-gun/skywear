package com.kaizen.skywear.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistItem
import com.kaizen.skywear.data.local.SkyWearDatabase
import com.kaizen.skywear.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 일본 여행 체크리스트 상태 관리

class ChecklistViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = ChecklistRepository(
        SkyWearDatabase.getDatabase(application).checklistDao()
    )

    // 전체 아이템 (Flow에서 StateFlow 변환)
    val allItems: StateFlow<List<ChecklistItem>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 완료 개수
    val checkedCount: StateFlow<Int> = repository.checkedCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // 전체 개수
    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // 선택된 카테고리 필터
    private val _selectedCategory = MutableStateFlow<ChecklistCategory?>(null)
    val selectedCategory: StateFlow<ChecklistCategory?> = _selectedCategory.asStateFlow()

    // 체크 토글
    fun toggleCheck(item: ChecklistItem) {
        viewModelScope.launch {
            repository.toggleCheck(item)
        }
    }

    // 커스텀 아이템 추가

    // 아이템 삭제

    // 체크된 아이템 전체 삭제

    // 기본 아이템으로 초기화

    // 카테고리 필터 변경

    // 진행률 계산 (%)
}