package com.kaizen.skywear

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kaizen.skywear.worker.NotificationScheduler
import com.kaizen.skywear.worker.SubscribeWeatherWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// -------------------------------------------------------
// 📱 SkyWearApplication (Updated)
// SubscribeWeatherWorker 스케줄 등록 추가
// -------------------------------------------------------

@HiltAndroidApp
class SkyWearApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    // ✅ Hilt WorkManager 설정
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Firebase 초기화
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
            setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        }

        // StrictMode (디버그 전용)
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
            Log.d("SkyWear", "Debug mode — Crashlytics collection disabled")
        }

        // ✅ 일일 날씨 브리핑 알림 (기존)
        NotificationScheduler.scheduleDailyBriefing(this)

        // ✅ 구독 도시 날씨 변화 알림 (신규)
        SubscribeWeatherWorker.schedule(this)
    }
}
