package com.kaizen.skywear.domain

import com.kaizen.skywear.data.model.Coord
import com.kaizen.skywear.data.model.MainWeather
import com.kaizen.skywear.data.model.Sys
import com.kaizen.skywear.data.model.Weather
import com.kaizen.skywear.data.model.WeatherResponse
import com.kaizen.skywear.data.model.Wind
import org.junit.Assert.assertEquals
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



    // OutfitGapLevel 단계 검증


    // 코디 차이 여부 검증


    // gapLabel 형식 검증


}