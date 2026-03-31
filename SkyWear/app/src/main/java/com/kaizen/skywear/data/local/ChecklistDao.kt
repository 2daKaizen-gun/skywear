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

    // 전체 아이템 조회 (카테고리 정리 -> 체크 안 된 것부터)
    @Query("SELECT * FROM checklist_items ORDER BY category ASC, isChecked ASC")
    fun getAllItems(): Flow<List<ChecklistItem>>

    // 카테고리별 조회
    @Query("SELECT * FROM checklist_items ")
    fun getItemsByCategory(category: ChecklistCategory): Flow<List<ChecklistItem>>

    // 체크 안 된 아이템만 조회
    @Query("SELECT * FROM checklist_items WHERE isChecked = 0")
    fun getUncheckedItems(): Flow<List<ChecklistItem>>

    // 완료된 아이템 수 조회
    @Query("SELECT * FROM checklist_items WHERE isChecked = 1")
    fun getCheckedCount(): Flow<Int>

    // 전체 아이템 수 조회
    @Query("SELECT * FROM checklist_items")
    fun getTotalCount(): Flow<Int>

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


    // 아이템 삭제
    @Delete
    suspend fun deleteItem(item: ChecklistItem)

    // 체크된 아이템 전체 삭제


    // 전체 초기화 (기본 아이템만 남기기)


}