package com.kaizen.skywear.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.repository.SubscribeRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt

// -------------------------------------------------------
// 🔔 SubscribeWeatherWorker
// 1시간마다 구독 도시 날씨 체크 → 3°C 이상 변화 시 알림
// -------------------------------------------------------

@HiltWorker
class SubscribeWeatherWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscribeRepository: SubscribeRepository,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME       = "subscribe_weather_check"
        const val CHANNEL_ID      = "subscribe_weather_channel"
        const val TEMP_DIFF_THRESHOLD = 3.0  // ✅ 3°C 이상 변화 시 알림

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SubscribeWeatherWorker>(
                1, TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val cities = subscribeRepository.getAlertEnabledCities()
        if (cities.isEmpty()) return Result.success()

        cities.forEach { city ->
            try {
                val result = weatherRepository.getWeather(city.nameEn)
                val weather = result.getOrNull() ?: return@forEach

                val newTemp    = weather.main.temp
                val prevTemp   = city.lastTemp
                val newDesc    = weather.weather.firstOrNull()?.description ?: ""
                val tempChange = abs(newTemp - prevTemp)

                // 이전 기록이 있고 3°C 이상 변화 시 알림
                // (lastTemp 기본값 0.0은 실제 0°C와 구분되지 않으므로 lastWeatherDesc로 기록 여부 판단)
                val hasPrevRecord = city.lastWeatherDesc.isNotEmpty()
                if (hasPrevRecord && tempChange >= TEMP_DIFF_THRESHOLD) {
                    val cityName   = localizedCityName(city.nameEn)
                    val messageRes = if (newTemp > prevTemp) R.string.notification_subscribe_temp_up
                                     else R.string.notification_subscribe_temp_down
                    val title      = context.getString(R.string.notification_subscribe_title, cityName)
                    val message    = context.getString(messageRes, prevTemp.roundToInt(), newTemp.roundToInt())

                    sendNotification(
                        id      = city.nameEn.hashCode(),
                        title   = title,
                        message = message
                    )
                }

                // ✅ 최신 날씨 DB 업데이트
                subscribeRepository.updateLastWeather(city.nameEn, newTemp, newDesc)

            } catch (e: Exception) {
                // 조용히 실패
            }
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_subscribe_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_subscribe_channel_desc)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(id: Int, title: String, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(id, notification)
    }
}
