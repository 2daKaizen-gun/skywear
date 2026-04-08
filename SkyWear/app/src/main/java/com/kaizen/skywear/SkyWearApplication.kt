package com.kaizen.skywear

import android.app.Application
import com.kaizen.skywear.worker.NotificationScheduler

// 앱 전역 초기화 담당
// WorkManager 알림 스케줄 등록

class SkyWearApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 앱 시작 시 매일 오전 8시 알림 스케줄 등록
        NotificationScheduler.scheduleDailyBriefing(this)
    }
}