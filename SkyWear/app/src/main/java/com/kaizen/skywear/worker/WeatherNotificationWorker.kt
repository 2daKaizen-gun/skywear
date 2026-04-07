package com.kaizen.skywear.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaizen.skywear.data.model.tempRounded
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import com.kaizen.skywear.domain.getOutfitRecommendation
import kotlinx.coroutines.flow.first

// WorkManager 기반 백그라운드 날씨 알림 서비스
// 매일 지정 시간에 한국/일본 날씨와 코디 브리핑 알림 발송

class WeatherNotificationWorker (
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "skywear_daily_briefing"
        const val CHANNEL_NAME = "SkyWear 데일리 브리핑"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            // DataStore에서 저장된 도시 불러오기
            val prefsRepo = UserPreferencesRepository(context)
            val krCity = prefsRepo.selectedKrCity.first()
            val jpCity = prefsRepo.selectedJpCity.first()

            // 날씨 데이터 호출
            val weatherRepo = WeatherRepository()
            val dualResult = weatherRepo.getDualCityWeather(krCity, jpCity)

            if (!dualResult.isSuccess) return Result.retry()

            val krWeather = dualResult.krWeather.getOrNull()!!
            val jpWeather = dualResult.jpWeather.getOrNull()!!

            // 코디 추천
            val krOutfit = getOutfitRecommendation(krWeather.main.temp)
            val jpOutfit = getOutfitRecommendation(jpWeather.main.temp)

            // 알림 메시지 구성하기
            val title = "\uD83C\uDF24\uFE0F SkyWear 오늘의 여행 브리핑"
            val message = buildNotificationMessage(
                krCity = krCity,
                jpCity = jpCity,
                krTemp = krWeather.tempRounded(),
                jpTemp = jpWeather.tempRounded(),
                krOutfit = krOutfit.mainOutfit,
                jpOutfit = jpOutfit.mainOutfit
            )

            // 알림 발송
            sendNotification(title, message)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    // 알림 메시지 빌드
    private fun buildNotificationMessage(
        krCity: String,
        jpCity: String,
        krTemp: Int,
        jpTemp: Int,
        krOutfit: String,
        jpOutfit: String
    ): String {
        val gap = jpTemp - krTemp
        val gapText = if (gap >= 0) "+${gap}°C" else "${gap}°C"

        return """
            🇰🇷 ${'$'}krCity ${'$'}{krTemp}°C → ${'$'}krOutfit
            🇯🇵 ${'$'}jpCity ${'$'}{jpTemp}°C → ${'$'}jpOutfit
            온도 차이: ${'$'}gapText
        """.trimIndent()
    }

    // 알림 채널 생성 및 발송
    private fun sendNotification(title: String, message: String) {


    }

}