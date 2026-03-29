package com.kaizen.skywear.data.local

import androidx.room.Dao

// Room DB 접근 인터페이스 - CRUD 쿼리 정의

@Dao
interface ChecklistDao {

    // 전체 아이템 조회 (카테고리 정리 -> 체크 안 된 것부터)

    // 카테고리별 조회

    // 체크 안 된 아이템만 조회

    // 완료된 아이템 수 조회

    // 전체 아이템 수 조회

    // 아이템 추가

    // 여러 아이템 한 번에 추가 (기본 아이템 초기화에 사용)

    // 아이템 수정

    // 체크 상태만 토글

    // 아이템 삭제

    // 체크된 아이템 전체 삭제

    // 전체 초기화 (기본 아이템만 남기기)
}