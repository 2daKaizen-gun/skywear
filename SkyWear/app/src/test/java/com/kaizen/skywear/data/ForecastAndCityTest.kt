package com.kaizen.skywear.data

import com.kaizen.skywear.data.model.ForecastItem
import com.kaizen.skywear.data.model.MainWeather
import com.kaizen.skywear.data.model.Weather
import com.kaizen.skywear.data.model.Wind
import com.kaizen.skywear.data.model.dailyRepresentativeWithTime
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.model.searchCities
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Locale

// 예보 일별 대표 슬롯 / 도시 검색·현지화 단위 테스트

class ForecastAndCityTest {

    private lateinit var originalLocale: Locale

    @Before fun saveLocale() { originalLocale = Locale.getDefault() }
    @After fun restoreLocale() { Locale.setDefault(originalLocale) }

    private fun slot(dateTime: String, temp: Double) = ForecastItem(
        timestamp = 0L,
        main = MainWeather(temp = temp, feelsLike = temp, tempMin = temp, tempMax = temp, humidity = 50, pressure = 1013),
        weather = listOf(Weather(id = 800, main = "Clear", description = "clear", icon = "01d")),
        wind = Wind(speed = 1.0, deg = 0),
        dateText = dateTime
    )

    @Test
    fun `대표 슬롯은 12시를 우선 선택하고 하루 최저 최고를 계산한다`() {
        val items = listOf(
            slot("2026-05-01 09:00:00", 10.0),
            slot("2026-05-01 12:00:00", 15.0),
            slot("2026-05-01 15:00:00", 18.4),
            slot("2026-05-01 21:00:00", 7.6)
        )
        val (rep, time, minMax) = items.dailyRepresentativeWithTime().getValue("2026-05-01")
        assertEquals("12:00", time)
        assertEquals(15.0, rep.main.temp, 0.0)
        assertEquals(7 to 18, minMax)
    }

    @Test
    fun `12시 슬롯이 없으면 15시를 대표로 선택한다`() {
        val items = listOf(slot("2026-05-02 03:00:00", 5.0), slot("2026-05-02 15:00:00", 12.0))
        val (_, time, _) = items.dailyRepresentativeWithTime().getValue("2026-05-02")
        assertEquals("15:00", time)
    }

    @Test
    fun `도시 검색은 한국어 영어 일본어 이름을 모두 지원한다`() {
        assertEquals("Osaka", searchCities("오사카", "JP").single().nameEn)
        assertEquals("Osaka", searchCities("osa", "JP").single().nameEn)
        assertEquals("Osaka", searchCities("大阪", "JP").single().nameEn)
        assertTrue(searchCities("Seoul", "JP").isEmpty())
    }

    @Test
    fun `빈 검색어는 해당 국가 전체 도시를 반환한다`() {
        val result = searchCities("", "KR")
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.country == "KR" })
    }

    @Test
    fun `도시명은 기기 언어에 맞게 현지화된다`() {
        Locale.setDefault(Locale.KOREAN)
        assertEquals("서울", localizedCityName("Seoul"))
        Locale.setDefault(Locale.JAPANESE)
        assertEquals("ソウル", localizedCityName("Seoul"))
        Locale.setDefault(Locale.ENGLISH)
        assertEquals("Seoul", localizedCityName("seoul"))
    }

    @Test
    fun `목록에 없는 도시명은 그대로 반환한다`() {
        assertEquals("Paris", localizedCityName("Paris"))
    }
}
