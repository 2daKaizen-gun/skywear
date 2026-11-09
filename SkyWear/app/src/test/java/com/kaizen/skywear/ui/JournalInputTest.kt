package com.kaizen.skywear.ui

import com.kaizen.skywear.ui.screen.JournalInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// 여행 일지 입력 검증 단위 테스트

class JournalInputTest {

    @Test
    fun `기온 입력은 부호 포함 2자리 정수만 허용한다`() {
        listOf("", "-", "5", "-5", "12", "-12").forEach { assertTrue(it, JournalInput.isAcceptableTempInput(it)) }
        listOf("abc", "123", "1.5", "--1", "5-", " 5").forEach { assertFalse(it, JournalInput.isAcceptableTempInput(it)) }
    }

    @Test
    fun `빈 값이나 부호만 있으면 기온은 0으로 저장된다`() {
        assertEquals(0, JournalInput.parseTemp(""))
        assertEquals(0, JournalInput.parseTemp("-"))
        assertEquals(-7, JournalInput.parseTemp("-7"))
    }

    @Test
    fun `DatePicker millis와 날짜 문자열은 서로 변환된다`() {
        val millis = JournalInput.dateStringToMillis("2026-05-01")!!
        assertEquals("2026-05-01", JournalInput.millisToDateString(millis))
        assertEquals(null, JournalInput.dateStringToMillis("05-01"))
    }

    @Test
    fun `귀국일은 출발일과 같거나 이후여야 한다`() {
        assertTrue(JournalInput.isValidDateRange("2026-05-01", "2026-05-01"))
        assertTrue(JournalInput.isValidDateRange("2026-05-01", "2026-05-05"))
        assertFalse(JournalInput.isValidDateRange("2026-05-05", "2026-05-01"))
        assertFalse(JournalInput.isValidDateRange("2026-05-01", "invalid"))
    }
}
