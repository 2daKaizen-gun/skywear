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
}