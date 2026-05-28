package com.kaizen.skywear.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.domain.getOutfitRecommendation
import kotlinx.coroutines.flow.first

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID      = "skywear_daily_briefing"
        const val CHANNEL_NAME    = "SkyWear 데일리 브리핑"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            val prefsRepo  = UserPreferencesRepository(context)
            val krCity     = prefsRepo.selectedKrCity.first()
            val jpCity     = prefsRepo.selectedJpCity.first()

            val weatherRepo = WeatherRepository()
            val dualResult  = weatherRepo.getDualCityWeather(krCity, jpCity)

            if (!dualResult.isSuccess) return Result.retry()

            val krWeather = dualResult.krWeather.getOrNull()!!
            val jpWeather = dualResult.jpWeather.getOrNull()!!

            val krTemp   = krWeather.tempRounded()
            val jpTemp   = jpWeather.tempRounded()
            val krOutfit = getOutfitRecommendation(krWeather.main.temp)
            val jpOutfit = getOutfitRecommendation(jpWeather.main.temp)

            // ✅ 현지화된 도시명 사용
            val krCityName = localizedCityName(krCity)
            val jpCityName = localizedCityName(jpCity)

            val gap     = jpTemp - krTemp
            val gapText = if (gap >= 0) "+${gap}°C" else "${gap}°C"

            val title   = "🌤️ SkyWear 오늘의 여행 브리핑"
            // ✅ 이스케이프 없이 직접 변수 삽입
            val message = "🇰🇷 $krCityName ${krTemp}°C → ${krOutfit.mainOutfit}\n" +
                    "🇯🇵 $jpCityName ${jpTemp}°C → ${jpOutfit.mainOutfit}\n" +
                    "온도 차이: $gapText"

            sendNotification(title, message)
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "매일 여행 날씨와 코디를 알려드려요" }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}