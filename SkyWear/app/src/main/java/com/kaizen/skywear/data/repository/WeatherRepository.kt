package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.remote.RetrofitClient
import com.kaizen.skywear.util.Constants

// KR/JP 날씨 데이터 호출 로직 담당
// ViewModel 에서 이 Repository만 보면 됨

class WeatherRepository {

    private val api = RetrofitClient.instance

    // KR 날씨 조회 (서울 위치 기반)
    suspend fun getKrWeather(
        city: String = Constants.DEFAULT_CITY_KR
    ): Result<WeatherResponse> {
        return try {
            val response = api.getWeatherByCity(
                cityName = city,
                lang = Constants.LANG_KR
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // JP 날씨 조회 (오사카 위치 기반)
    suspend fun getJpWeather(
        city: String = Constants.DEFAULT_CITY_JP
    ): Result<WeatherResponse> {
        return try {
            val response = api.getWeatherByCity(
                cityName = city,
                lang = Constants.LANG_KR // 한국어로 날씨 설명 받기
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // KR + JP 동시 조회(Dual-City Dashboard 핵심 로직)
    suspend fun getDualCityWeather(
        krCity: String = Constants.DEFAULT_CITY_KR,
        jpCity: String = Constants.DEFAULT_CITY_JP
    ): DualCityResult {
        val krResult = getKrWeather(krCity)
        val jpResult = getJpWeather(jpCity)

        return DualCityResult(
            krWeather = krResult,
            jpWeather = jpResult
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