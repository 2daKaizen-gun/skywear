package com.kaizen.skywear.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kaizen.skywear.data.local.SkyWearDatabase
import com.kaizen.skywear.data.repository.ChecklistRepository

// 일본 여행 체크리스트 상태 관리

class ChecklistViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = ChecklistRepository(
        SkyWearDatabase.getDatabase(application).checklistDao()
    )

    // 전체 아이템 (Flow에서 StateFlow 변환)

    // 완료 개수

    // 전체 개수

    // 선택된 카테고리 필터

    // 체크 토글

    // 커스텀 아이템 추가

    // 아이템 삭제

    // 체크된 아이템 전체 삭제

    // 기본 아이템으로 초기화

    // 카테고리 필터 변경

    // 진행률 계산 (%)
}