package com.kaizen.skywear.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// WorkManager 알림 스케줄 관리
// 매일 오전 8시, 날씨 브리핑 알림 발송

object NotificationScheduler {
    private const val WORK_NAME = "skywear_daily_briefing_work"

    // 매일 알림 등록 (앱 실행 시 또는 설정에서 호출)
    fun scheduleDailyBriefing(context: Context) {

        // 네트워크 연결 필수 조건
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 다음 오전 8시까지 남은 시간 계산
        val initialDelay

        // 24시간마다 반복 작업 등록
        val workRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        // KEEP: 이미 등록된 작업 있으면 유지 (중복 방지)

    }

    // 알림 취소
    fun cancelDailyBriefing(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    // 다음 목표 시각까지 남은 밀리초 계산


}