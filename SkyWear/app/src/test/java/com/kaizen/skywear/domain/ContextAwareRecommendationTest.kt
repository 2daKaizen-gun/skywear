package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.Coord
import com.kaizen.skywear.data.model.MainWeather
import com.kaizen.skywear.data.model.Sys
import com.kaizen.skywear.data.model.Weather
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.Wind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// 체감온도 / 습도 / 바람 보정 로직 단위 테스트

class ContextAwareRecommendationTest {

    private fun makeWeather(temp: Double, humidity: Int = 50, windSpeed: Double = 0.0, weatherId: Int = 800) = WeatherResponse(
        cityName = "Seoul",
        main = MainWeather(temp = temp, feelsLike = temp, tempMin = temp, tempMax = temp, humidity = humidity, pressure = 1013),
        weather = listOf(Weather(id = weatherId, main = "", description = "", icon = "01d")),
        wind = Wind(speed = windSpeed, deg = 0),
        sys = Sys(country = "KR"),
        coord = Coord(lat = 0.0, lon = 0.0),
        timestamp = 0L
    )

    @Test
    fun `추운 날 바람이 강하면 체감온도가 실제 기온보다 낮다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 2.0, windSpeed = 8.0))
        assertTrue(result.feelsLikeTemp < 2.0)
        assertEquals(WindLevel.WINDY, result.windLevel)
    }

    @Test
    fun `바람이 약하면 추운 날에도 체감온도는 실제 기온과 같다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 2.0, windSpeed = 1.0))
        assertEquals(2.0, result.feelsLikeTemp, 0.0001)
    }

    @Test
    fun `온화한 기온에서는 체감온도 보정이 없다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 18.0, humidity = 90, windSpeed = 12.0))
        assertEquals(18.0, result.feelsLikeTemp, 0.0001)
    }

    @Test
    fun `더운 날에는 Heat Index로 체감온도가 보정된다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 32.0, humidity = 85))
        assertNotEquals(32.0, result.feelsLikeTemp, 0.0001)
        assertEquals(HumidityLevel.VERY_HUMID, result.humidityLevel)
    }

    @Test
    fun `습도 단계 경계값 분류`() {
        assertEquals(HumidityLevel.DRY, buildContextAwareRecommendation(makeWeather(15.0, humidity = 39)).humidityLevel)
        assertEquals(HumidityLevel.COMFORTABLE, buildContextAwareRecommendation(makeWeather(15.0, humidity = 60)).humidityLevel)
        assertEquals(HumidityLevel.HUMID, buildContextAwareRecommendation(makeWeather(15.0, humidity = 80)).humidityLevel)
        assertEquals(HumidityLevel.VERY_HUMID, buildContextAwareRecommendation(makeWeather(15.0, humidity = 81)).humidityLevel)
    }

    @Test
    fun `체감온도가 영하면 핫팩을 추천한다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = -3.0, windSpeed = 5.0))
        assertTrue(result.extraItems.contains(ExtraItem.HAND_WARMER))
    }

    @Test
    fun `비 날씨에만 우산을 추천한다`() {
        val rainy = buildContextAwareRecommendation(makeWeather(temp = 15.0, weatherId = 501))
        val drizzle = buildContextAwareRecommendation(makeWeather(temp = 15.0, weatherId = 300))
        val clearButHumid = buildContextAwareRecommendation(makeWeather(temp = 15.0, humidity = 90, weatherId = 800))
        assertTrue(rainy.extraItems.contains(ExtraItem.UMBRELLA))
        assertTrue(drizzle.extraItems.contains(ExtraItem.UMBRELLA))
        assertFalse(clearButHumid.extraItems.contains(ExtraItem.UMBRELLA))
        assertTrue(clearButHumid.extraItems.contains(ExtraItem.MOISTURE_WICKING_CLOTHES))
    }

    @Test
    fun `쾌적한 날씨에는 추가 아이템이 없다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 18.0, humidity = 50, windSpeed = 1.0))
        assertTrue(result.extraItems.isEmpty())
    }

    @Test
    fun `보정 코디는 체감온도 기준 단계를 따른다`() {
        val result = buildContextAwareRecommendation(makeWeather(temp = 1.0, windSpeed = 10.0))
        assertEquals(getOutfitRecommendation(result.feelsLikeTemp).stage, result.adjustedOutfit.stage)
        assertEquals(getOutfitRecommendation(1.0).stage, result.baseOutfit.stage)
    }
}
