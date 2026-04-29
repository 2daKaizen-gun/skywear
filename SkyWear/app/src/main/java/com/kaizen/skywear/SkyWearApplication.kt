package com.kaizen.skywear

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kaizen.skywear.worker.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp

// 앱 전역 초기화 담당
// WorkManager 알림 스케줄 등록

@HiltAndroidApp
class SkyWearApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // Crashlytics 설정
        FirebaseCrashlytics.getInstance().apply {
            // 릴리스 빌드에서만 크래시 수집
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            // 앱 버전 정보 추가
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
            setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        }

        // 디버그 빌드에서만 StrictMode 활성화
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
            Log.d("SkyWear", "Debug mode - Crashlytics collection disabled")
        }

        // 앱 시작 시 매일 오전 8시 알림 스케줄 등록
        NotificationScheduler.scheduleDailyBriefing(this)
    }
}