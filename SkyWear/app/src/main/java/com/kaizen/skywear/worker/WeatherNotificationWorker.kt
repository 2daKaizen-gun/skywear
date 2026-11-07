package com.kaizen.skywear.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.domain.getOutfitRecommendation
import com.kaizen.skywear.domain.mainOutfitRes
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

// 매일 오전 8시 KR/JP 날씨 + 코디 브리핑 알림
@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefsRepo: UserPreferencesRepository,
    private val weatherRepo: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID      = "skywear_daily_briefing"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            val krCity = prefsRepo.selectedKrCity.first()
            val jpCity = prefsRepo.selectedJpCity.first()

            val dualResult = weatherRepo.getDualCityWeather(krCity, jpCity)
            val krWeather  = dualResult.krWeather.getOrNull()
            val jpWeather  = dualResult.jpWeather.getOrNull()
            if (krWeather == null || jpWeather == null) return Result.retry()

            val krTemp   = krWeather.tempRounded()
            val jpTemp   = jpWeather.tempRounded()
            val krOutfit = context.getString(getOutfitRecommendation(krWeather.main.temp).mainOutfitRes())
            val jpOutfit = context.getString(getOutfitRecommendation(jpWeather.main.temp).mainOutfitRes())

            // 현지화된 도시명 사용
            val krCityName = localizedCityName(krCity)
            val jpCityName = localizedCityName(jpCity)

            val gap     = jpTemp - krTemp
            val gapText = if (gap >= 0) "+${gap}°C" else "${gap}°C"

            val title   = context.getString(R.string.notification_title)
            val message = "🇰🇷 $krCityName ${krTemp}°C → $krOutfit\n" +
                    "🇯🇵 $jpCityName ${jpTemp}°C → $jpOutfit\n" +
                    context.getString(R.string.notification_briefing_gap, gapText)

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
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = context.getString(R.string.notification_channel_desc) }
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
