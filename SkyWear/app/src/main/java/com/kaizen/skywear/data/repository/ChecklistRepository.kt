package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistDao
import com.kaizen.skywear.data.local.ChecklistItem
import kotlinx.coroutines.flow.Flow

// ViewModel -> DAO 사이의 데이터 접근 계층

class ChecklistRepository(private val dao: ChecklistDao) {

    // 전체 아이템 Flow (실시간 업데이트)
    val allItems: Flow<List<ChecklistItem>> = dao.getAllItems()

    // 완료된 아이템 수
    val checkedCount: Flow<Int> = dao.getCheckedCount()

    // 전체 아이템 수
    val totalCount: Flow<Int> = dao.getTotalCount()

    // 카테고리별 조회
    fun getItemsByCategory(category: ChecklistCategory): Flow<List<ChecklistItem>> =
        dao.getItemsByCategory(category)

    // 아이템 추가

    // 체크 상태 토글

    // 아이템 수정

    // 아이템 삭제

    // 체크된 아이템 전체 삭제

    // 커스텀 아이템만 삭제 (기본 아이템은 유지)
}