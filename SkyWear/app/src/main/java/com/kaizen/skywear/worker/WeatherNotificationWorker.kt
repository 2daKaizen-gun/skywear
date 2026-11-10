package com.kaizen.skywear.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.repository.TravelDirection
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
            val krCity    = prefsRepo.selectedKrCity.first()
            val jpCity    = prefsRepo.selectedJpCity.first()
            val isKrToJp  = prefsRepo.travelDirection.first() == TravelDirection.KR_TO_JP

            val dualResult = weatherRepo.getDualCityWeather(krCity, jpCity)
            val krWeather  = dualResult.krWeather.getOrNull()
            val jpWeather  = dualResult.jpWeather.getOrNull()
            if (krWeather == null || jpWeather == null) return Result.retry()

            // 대시보드와 동일하게 여행 방향 기준으로 출발지 → 도착지 순서로 표시
            val krLine = briefingLine("🇰🇷", localizedCityName(krCity), krWeather)
            val jpLine = briefingLine("🇯🇵", localizedCityName(jpCity), jpWeather)
            val (depLine, dstLine) = if (isKrToJp) krLine to jpLine else jpLine to krLine

            // 온도 차이 = 도착지 - 출발지
            val krTemp  = krWeather.tempRounded()
            val jpTemp  = jpWeather.tempRounded()
            val gap     = if (isKrToJp) jpTemp - krTemp else krTemp - jpTemp
            val gapText = if (gap >= 0) "+${gap}°C" else "${gap}°C"

            val title   = context.getString(R.string.notification_title)
            val message = "$depLine\n$dstLine\n" +
                    context.getString(R.string.notification_briefing_gap, gapText)

            sendNotification(title, message)
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }

    // "🇰🇷 서울 3°C → 롱패딩 + 목도리" 형식의 한 줄
    private fun briefingLine(flag: String, cityName: String, weather: WeatherResponse): String {
        val outfit = context.getString(getOutfitRecommendation(weather.main.temp).mainOutfitRes())
        return "$flag $cityName ${weather.tempRounded()}°C → $outfit"
    }

    private fun sendNotification(title: String, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // minSdk 26 (Android 8.0) 이상이므로 알림 채널은 항상 생성
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = context.getString(R.string.notification_channel_desc) }
        manager.createNotificationChannel(channel)

        val notification = buildSkyWearNotification(context, CHANNEL_ID, title, message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}
