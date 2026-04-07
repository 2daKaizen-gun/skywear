package com.kaizen.skywear.worker

import android.content.Context

// WorkManager 알림 스케줄 관리
// 매일 오전 8시, 날씨 브리핑 알림 발송

object NotificationScheduler {
    private const val WORK_NAME = "skywear_daily_briefing_work"

    // 매일 알림 등록 (앱 실행 시 또는 설정에서 호출)
    fun scheduleDailyBriefing(context: Context) {

        // 네트워크 연결 필수 조건

        // 다음 오전 8시까지 남은 시간 계산

        // 24시간마다 반복 작업 등록

        // KEEP: 이미 등록된 작업 있으면 유지 (중복 방지)

    }

    // 알림 취소

    // 다음 목표 시각까지 남은 밀리초 계산


}