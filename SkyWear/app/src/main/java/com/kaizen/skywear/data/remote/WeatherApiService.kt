package com.kaizen.skywear.data.remote

import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

// OpenWeatherMap API 엔드포인트 정의
interface WeatherApiService {
    // 도시명으로 현재 날씨 조회
    // GET https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=KEY&units=metric&lang=kr
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = Constants.WEATHER_API_KEY,
        @Query("units") units: String = Constants.UNIT,
        @Query("lang") lang: String = Constants.LANG_KR
    ): WeatherResponse

    // 위도/경도로 현재 날씨 조회
    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.WEATHER_API_KEY,
        @Query("units") units: String = Constants.UNIT,
        @Query("lang") lang: String = Constants.LANG_KR
    ): WeatherResponse
}