package com.kaizen.skywear.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistItem
import com.kaizen.skywear.data.repository.ChecklistRepository
import com.kaizen.skywear.data.repository.TravelDirection
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 일본 여행 체크리스트 상태 관리
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val repository: ChecklistRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    // 여행 방향 StateFlow
    val travelDirection: StateFlow<TravelDirection> = prefsRepository.travelDirection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
            TravelDirection.KR_TO_JP)

    // 방향에 따른 destination 문자열
    private val currentDestination = travelDirection.map { direction ->
        if (direction == TravelDirection.KR_TO_JP) "JP" else "KR"
    }

    // 방향 바뀌면 자동으로 다른 체크리스트 로드
    val allItems: StateFlow<List<ChecklistItem>> = currentDestination
        .flatMapLatest { dest -> repository.getItemsByDestination(dest) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 완료 개수
    val checkedCount: StateFlow<Int> = currentDestination
        .flatMapLatest { dest -> repository.getCheckedCount(dest) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),0)

    // 전체 개수
    val totalCount: StateFlow<Int> = currentDestination
        .flatMapLatest { dest -> repository.getTotalCount(dest) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

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
            val destination = if (travelDirection.value == TravelDirection.KR_TO_JP) "JP" else "KR"
            repository.addItem(
                ChecklistItem(
                    title = title,
                    category = category,
                    isDefault = false,
                    memo = memo,
                    destination = destination
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
            val destination = if (travelDirection.value == TravelDirection.KR_TO_JP) "JP" else "KR"
            repository.deleteCheckedItems(destination)
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