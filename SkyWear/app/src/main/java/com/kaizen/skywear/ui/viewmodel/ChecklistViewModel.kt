package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistItem
import com.kaizen.skywear.data.repository.ChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 일본 여행 체크리스트 상태 관리

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val repository: ChecklistRepository
) : ViewModel() {

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
    fun addItem(title: String, category: ChecklistCategory, memo: String = "") {
        viewModelScope.launch {
            repository.addItem(
                ChecklistItem(
                    title = title,
                    category = category,
                    isDefault = false,
                    memo = memo
                )
            )
        }
    }

    // 아이템 삭제
    fun deleteItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    // 체크된 아이템 전체 삭제
    fun deleteCheckedItems() {
        viewModelScope.launch {
            repository.deleteCheckedItems()
        }
    }

    // 기본 아이템으로 초기화
    fun resetToDefault() {
        viewModelScope.launch {
            repository.resetToDefault()
        }
    }

    // 카테고리 필터 변경
    fun filterByCategory(category: ChecklistCategory?) {
        _selectedCategory.value = category
    }

    // 진행률 계산 (%)
    fun getProgress(checked: Int, total: Int): Float {
        return if (total == 0) 0f else checked.toFloat() / total.toFloat()
    }
}