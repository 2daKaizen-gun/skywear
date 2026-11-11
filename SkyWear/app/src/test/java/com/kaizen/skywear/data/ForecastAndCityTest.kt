package com.kaizen.skywear.data

import com.kaizen.skywear.data.model.ForecastItem
import com.kaizen.skywear.data.model.MainWeather
import com.kaizen.skywear.data.model.Weather
import com.kaizen.skywear.data.model.Wind
import com.kaizen.skywear.data.model.dailyRepresentativeWithTime
import com.kaizen.skywear.data.model.dateKey
import com.kaizen.skywear.data.model.dateLabel
import com.kaizen.skywear.data.model.localizedCityName
import com.kaizen.skywear.data.model.timeLabel
import com.kaizen.skywear.data.model.searchCities
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Locale

// 예보 일별 대표 슬롯 / 도시 검색·현지화 단위 테스트

class ForecastAndCityTest {

    private lateinit var originalLocale: Locale

    @Before fun saveLocale() { originalLocale = Locale.getDefault() }
    @After fun restoreLocale() { Locale.setDefault(originalLocale) }

    // KR/JP UTC 오프셋 (UTC+9)
    private val kstOffset = 9 * 3600

    // 현지(UTC+9) 시각 "yyyy-MM-ddTHH:mm" 기준 슬롯 — API처럼 dt는 UTC epoch, dt_txt는 UTC 문자열
    private fun slot(localDateTime: String, temp: Double): ForecastItem {
        val utc = LocalDateTime.parse(localDateTime).minusHours(9)
        return ForecastItem(
            timestamp = utc.toEpochSecond(ZoneOffset.UTC),
            main = MainWeather(temp = temp, feelsLike = temp, tempMin = temp, tempMax = temp, humidity = 50, pressure = 1013),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear", icon = "01d")),
            wind = Wind(speed = 1.0, deg = 0),
            dateText = utc.toString().replace('T', ' ') + ":00"
        )
    }

    @Test
    fun `대표 슬롯은 12시를 우선 선택하고 하루 최저 최고를 반올림해 계산한다`() {
        val items = listOf(
            slot("2026-05-01T09:00", 10.0),
            slot("2026-05-01T12:00", 15.0),
            slot("2026-05-01T15:00", 18.4),
            slot("2026-05-01T21:00", 7.6)
        )
        val (rep, time, minMax) = items.dailyRepresentativeWithTime(kstOffset).getValue("2026-05-01")
        assertEquals("12:00", time)
        assertEquals(15.0, rep.main.temp, 0.0)
        assertEquals(8 to 18, minMax)
    }

    @Test
    fun `12시 슬롯이 없으면 15시를 대표로 선택한다`() {
        val items = listOf(slot("2026-05-02T03:00", 5.0), slot("2026-05-02T15:00", 12.0))
        val (_, time, _) = items.dailyRepresentativeWithTime(kstOffset).getValue("2026-05-02")
        assertEquals("15:00", time)
    }

    @Test
    fun `예보 날짜와 시간은 UTC dt_txt가 아니라 도시 현지 시각 기준이다`() {
        // 현지 2026-05-01 06:00 = UTC 2026-04-30 21:00
        val early = slot("2026-05-01T06:00", 3.0)
        assertEquals("2026-04-30 21:00:00", early.dateText)
        assertEquals("2026-05-01", early.dateKey(kstOffset))
        assertEquals("05/01", early.dateLabel(kstOffset))
        assertEquals("06:00", early.timeLabel(kstOffset))

        // UTC 03:00 슬롯은 현지 12:00 이므로 대표 슬롯으로 선택됨
        val noon = slot("2026-05-01T12:00", 20.0)
        val (rep, time, _) = listOf(early, noon).dailyRepresentativeWithTime(kstOffset).getValue("2026-05-01")
        assertEquals("12:00", time)
        assertEquals(20.0, rep.main.temp, 0.0)
    }

    @Test
    fun `영하 최저기온도 반올림한다`() {
        val items = listOf(slot("2026-01-10T06:00", -0.6), slot("2026-01-10T12:00", 2.5))
        val (_, _, minMax) = items.dailyRepresentativeWithTime(kstOffset).getValue("2026-01-10")
        assertEquals(-1 to 3, minMax)
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
