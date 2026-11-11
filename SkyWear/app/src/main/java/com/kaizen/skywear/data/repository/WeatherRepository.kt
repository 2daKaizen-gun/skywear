package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.model.DualForecastResult
import com.kaizen.skywear.data.model.WeatherForecastResponse
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.remote.WeatherApiService
import com.kaizen.skywear.util.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// KR/JP 날씨 데이터 호출 로직 담당
// ViewModel 에서 이 Repository만 보면 됨

class WeatherRepository @Inject constructor(
    private val api: WeatherApiService
) {

    // 기기 언어 -> OpenWeatherMap lang 코드
    private fun getApiLang(): String = when (Locale.getDefault().language) {
        "ja" -> "ja"
        "en" -> "en"
        else -> Constants.LANG_KR
    }

    // 도시명으로 현재 날씨 조회 (KR/JP/구독 도시 공통)
    suspend fun getWeather(city: String): Result<WeatherResponse> = apiResult {
        api.getWeatherByCity(cityName = city, lang = getApiLang())
    }

    // 도시명으로 5일 예보 조회
    suspend fun getForecast(city: String): Result<WeatherForecastResponse> = apiResult {
        api.getForecastByCity(cityName = city, lang = getApiLang())
    }

    // KR + JP 동시 조회(Dual-City Dashboard 핵심 로직) — 두 요청을 병렬로 실행
    suspend fun getDualCityWeather(
        krCity: String = Constants.DEFAULT_CITY_KR,
        jpCity: String = Constants.DEFAULT_CITY_JP
    ): DualCityResult = coroutineScope {
        val kr = async { getWeather(krCity) }
        val jp = async { getWeather(jpCity) }
        DualCityResult(krWeather = kr.await(), jpWeather = jp.await())
    }

    // KR + JP 예보 동시 호출 — 두 요청을 병렬로 실행
    suspend fun getDualCityForecast(
        krCity: String = Constants.DEFAULT_CITY_KR,
        jpCity: String = Constants.DEFAULT_CITY_JP
    ): DualForecastResult = coroutineScope {
        val kr = async { getForecast(krCity) }
        val jp = async { getForecast(jpCity) }
        DualForecastResult(krForecast = kr.await(), jpForecast = jp.await())
    }
}

// runCatching과 같지만 코루틴 취소(CancellationException)는 실패로 바꾸지 않고 그대로 전파
// (runCatching은 취소까지 Result.failure로 삼켜서, 취소된 요청이 에러 상태를 화면에 쓰게 됨)
internal inline fun <T> apiResult(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
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

    // 둘 중 하나라도 실패 시 해당 예외 반환
    val error: Throwable?
        get() = krWeather.exceptionOrNull() ?: jpWeather.exceptionOrNull()
}
