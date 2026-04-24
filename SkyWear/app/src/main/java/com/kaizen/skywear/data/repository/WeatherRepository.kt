package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.remote.NetworkException
import com.kaizen.skywear.data.remote.RetrofitClient
import com.kaizen.skywear.util.Constants
import java.util.Locale

// KR/JP 날씨 데이터 호출 로직 담당
// ViewModel 에서 이 Repository만 보면 됨

class WeatherRepository {

    private val api = RetrofitClient.instance

    // 기기 언어 -> OpenWeatherMap lang 코드
    private fun getApiLang(): String = when (Locale.getDefault().language) {
        "ja" -> "ja"
        "en" -> "en"
        else -> "kr"
    }

    // KR 날씨 조회 (서울 위치 기반)
    suspend fun getKrWeather(
        city: String = Constants.DEFAULT_CITY_KR
    ): Result<WeatherResponse> {
        return try {
            Result.success(api.getWeatherByCity(
                cityName = city,
                lang = getApiLang()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // JP 날씨 조회 (오사카 위치 기반)
    suspend fun getJpWeather(
        city: String = Constants.DEFAULT_CITY_JP
    ): Result<WeatherResponse> {
        return try {
            Result.success(api.getWeatherByCity(
                cityName = city,
                lang = getApiLang()))
        } catch (e: NetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // KR + JP 동시 조회(Dual-City Dashboard 핵심 로직)
    suspend fun getDualCityWeather(
        krCity: String = Constants.DEFAULT_CITY_KR,
        jpCity: String = Constants.DEFAULT_CITY_JP
    ): DualCityResult {
        return DualCityResult(
            getKrWeather(krCity),
            getJpWeather(jpCity)
        )
    }
}

// DualCityResult
// KR/JP 날씨 결과를 한번에 담는 데이터 클래스

data class DualCityResult(
    val krWeather: Result<WeatherResponse>,
    val jpWeather: Result<WeatherResponse>
) {
    // 둘 다 성공 시 true
    val isSuccess: Boolean
        get() = krWeather.isSuccess && jpWeather.isSuccess

    // 둘 중 하나라도 실패 시 반환
    val errorMessage: String?
        get() = krWeather.exceptionOrNull()?.message
            ?: jpWeather.exceptionOrNull()?.message
}