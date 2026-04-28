package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.Coord
import com.kaizen.skywear.data.model.MainWeather
import com.kaizen.skywear.data.model.Sys
import com.kaizen.skywear.data.model.Weather
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.Wind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Phase 3-2 TemperatureComparator 단위 테스트

class TemperatureComparatorTest {

    // 테스트용 WeatherResponse 생성 헬퍼
    private fun makeWeather(
        cityName: String,
        temp: Double,
        country: String = "KR"
    ) = WeatherResponse(
        cityName = cityName,
        main = MainWeather(
            temp = temp,
            feelsLike = temp - 2.0,
            tempMin = temp - 3.0,
            tempMax = temp + 3.0,
            humidity = 60,
            pressure = 1013
        ),
        weather = listOf(Weather(id = 800, main = "Clear", description = "맑음", icon = "01d")),
        wind = Wind(speed = 3.0, deg = 180),
        sys = Sys(country = country),
        coord = Coord(lat = 37.5, lon = 126.9),
        timestamp = System.currentTimeMillis()
    )

    // 온도 차이 계산 정확성 검증
    @Test
    fun `JP가 KR보다 12도 높으면 gapDegree가 12`() {
        val kr = makeWeather("Seoul", -2.0, "KR")
        val jp = makeWeather("Osaka", 10.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertEquals(12, result.gapDegree)
    }

    @Test
    fun `JP가 KR보다 낮으면 gapDegree가 음수`() {
        val kr = makeWeather("Seoul", 10.0, "KR")
        val jp = makeWeather("Osaka", 5.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertTrue(result.gapDegree < 0)
    }

    @Test
    fun `온도가 같으면 gapDegree가 0`() {
        val kr = makeWeather("Seoul", 10.0)
        val jp = makeWeather("Osaka", 10.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertEquals(0, result.gapDegree)
    }

    // OutfitGapLevel 단계 검증
    @Test
    fun `3도 이하 차이면 SIMILAR`() {
        val kr = makeWeather("Seoul", 10.0)
        val jp = makeWeather("Osaka", 12.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertEquals(OutfitGapLevel.SIMILAR, result.outfitGapLevel)
    }

    @Test
    fun `4도에서 9도 차이면 MODERATE`() {
        val kr = makeWeather("Seoul", 10.0)
        val jp = makeWeather("Osaka", 16.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertEquals(OutfitGapLevel.MODERATE, result.outfitGapLevel)
    }

    @Test
    fun `10도 이상 차이나면 SIGNIFICANT`() {
        val kr = makeWeather("Seoul", -2.0)
        val jp = makeWeather("Osaka", 10.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertEquals(OutfitGapLevel.SIGNIFICANT, result.outfitGapLevel)
    }

    // 코디 차이 여부 검증
    @Test
    fun `온도 차이로 코디 단계 달라지면 isOutfitDifferent가 true`() {
        val kr = makeWeather("Seoul", -2.0)
        val jp = makeWeather("Osaka", 20.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertTrue(result.isOutfitDifferent())
    }

    @Test
    fun `온도 차이가 없으면 isOutfitDifferent가 false`() {
        val kr = makeWeather("Seoul", 15.0)
        val jp = makeWeather("Osaka", 15.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertFalse(result.isOutfitDifferent())
    }

    // gapLabel 형식 검증
    @Test
    fun `JP가 높으면 gapLabel이 플러스 기호 포함`() {
        val kr = makeWeather("Seoul", 0.0)
        val jp = makeWeather("Osaka", 10.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertTrue(result.directedGapLabel(isKrToJp = true).startsWith("+"))
    }

    @Test
    fun `JP가 낮으면 gapLabel이 마이너스 기호 포함`() {
        val kr = makeWeather("Seoul", 10.0)
        val jp = makeWeather("Osaka", 0.0, "JP")
        val result = analyzeTempComparison(kr, jp)
        assertTrue(result.directedGapLabel(isKrToJp = true).startsWith("-"))
    }
}