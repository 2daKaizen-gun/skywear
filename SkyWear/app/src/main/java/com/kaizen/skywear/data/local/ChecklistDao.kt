package com.kaizen.skywear.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// Room DB 접근 인터페이스 - CRUD 쿼리 정의

@Dao
interface ChecklistDao {

    // 여행 방향별 조회
    @Query("SELECT * FROM checklist_items WHERE destination = :destination ORDER BY category ASC, isChecked ASC")
    fun getItemsByDestination(destination: String): Flow<List<ChecklistItem>>

    // 여행 방향별 완료 수
    @Query("SELECT COUNT(*) FROM checklist_items WHERE destination = :destination AND isChecked = 1")
    fun getCheckedCount(destination: String): Flow<Int>

    // 여행 방향별 전체 수
    @Query("SELECT COUNT(*) FROM checklist_items WHERE destination = :destination")
    fun getTotalCount(destination: String): Flow<Int>

    // 아이템 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItem)

    // 여러 아이템 한 번에 추가 (기본 아이템 초기화에 사용)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<ChecklistItem>)

    // 체크 상태만 토글
    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateChecked(id: Int, isChecked: Boolean)

    // 아이템 삭제
    @Delete
    suspend fun deleteItem(item: ChecklistItem)

    // 체크된 커스텀 아이템 삭제
    @Query("DELETE FROM checklist_items WHERE destination = :destination AND isChecked = 1 AND isDefault = 0")
    suspend fun deleteCheckedCustomItems(destination: String)

    // 체크된 기본 아이템은 삭제하지 않고 체크만 해제
    // (기본 아이템은 DB 최초 생성 시에만 추가되므로 지우면 복구할 방법이 없음)
    @Query("UPDATE checklist_items SET isChecked = 0 WHERE destination = :destination AND isChecked = 1 AND isDefault = 1")
    suspend fun uncheckDefaultItems(destination: String)

    // 완료 항목 정리 (커스텀 삭제 + 기본 체크 해제)를 한 트랜잭션으로 처리
    @Transaction
    suspend fun clearCheckedItems(destination: String) {
        deleteCheckedCustomItems(destination)
        uncheckDefaultItems(destination)
    }

}