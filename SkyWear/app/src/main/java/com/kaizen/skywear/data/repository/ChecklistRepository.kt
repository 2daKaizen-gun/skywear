package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.local.ChecklistCategory
import com.kaizen.skywear.data.local.ChecklistDao
import com.kaizen.skywear.data.local.ChecklistItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ViewModel -> DAO 사이의 데이터 접근 계층

class ChecklistRepository @Inject constructor(private val dao: ChecklistDao) {

    // 여행 방향별 아이템 조회
    fun getItemsByDestination(destination: String): Flow<List<ChecklistItem>> =
        dao.getItemsByDestination(destination)

    fun getCheckedCount(destination: String): Flow<Int> =
        dao.getCheckedCount(destination)

    fun getTotalCount(destination: String): Flow<Int> =
        dao.getTotalCount(destination)

    // 카테고리별 조회
    fun getItemsByCategory(destination: String, category: ChecklistCategory): Flow<List<ChecklistItem>> =
        dao.getItemsByDestinationAndCategory(destination, category)

    // 아이템 추가
    suspend fun addItem(item: ChecklistItem) = dao.insertItem(item)

    // 체크 상태 토글
    suspend fun toggleCheck(item: ChecklistItem) =
        dao.updateChecked(item.id, !item.isChecked)

    // 아이템 수정
    suspend fun updateItem(item: ChecklistItem) = dao.updateItem(item)

    // 아이템 삭제
    suspend fun deleteItem(item: ChecklistItem) = dao.deleteItem(item)

    // 체크된 아이템 전체 삭제
    suspend fun deleteCheckedItems(destination: String) = dao.deleteCheckedItems(destination)

    // 커스텀 아이템만 삭제 (기본 아이템은 유지)
    suspend fun resetToDefault() = dao.deleteCustomItems()
}