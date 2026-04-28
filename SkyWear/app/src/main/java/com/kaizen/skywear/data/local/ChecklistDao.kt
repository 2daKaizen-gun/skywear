package com.kaizen.skywear.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Room DB 접근 인터페이스 - CRUD 쿼리 정의

@Dao
interface ChecklistDao {

    // 여행 방향별 조회
    @Query("SELECT * FROM checklist_items WHERE destination = :destination ORDER BY category ASC, isChecked ASC")
    fun getItemsByDestination(destination: String): Flow<List<ChecklistItem>>

    // 여행 방향 + 카테고리 조회
    @Query("SELECT * FROM checklist_items WHERE destination = :destination AND category = :category ORDER BY isChecked ASC")
    fun getItemsByDestinationAndCategory(destination: String, category: ChecklistCategory): Flow<List<ChecklistItem>>

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

    // 아이템 수정
    @Update
    suspend fun updateItem(item: ChecklistItem)

    // 체크 상태만 토글
    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateChecked(id: Int, isChecked: Boolean)

    // 아이템 삭제
    @Delete
    suspend fun deleteItem(item: ChecklistItem)

    // 체크된 아이템 전체 삭제
    @Query("DELETE FROM checklist_items WHERE destination = :destination AND isChecked = 1")
    suspend fun deleteCheckedItems(destination: String)

    // 전체 초기화 (기본 아이템만 남기기)
    @Query("DELETE FROM checklist_items WHERE isDefault = 0")
    suspend fun deleteCustomItems()

}