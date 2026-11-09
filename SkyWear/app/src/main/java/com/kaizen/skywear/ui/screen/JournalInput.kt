package com.kaizen.skywear.ui.screen

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// 여행 일지 입력값 검증 / 변환 (UI와 분리해 단위 테스트 가능하도록)
object JournalInput {

    // 기온 입력 허용 형식: 빈 값, "-", 최대 2자리 정수 (부호 포함) ex) "12", "-5"
    private val TEMP_PATTERN = Regex("^-?\\d{0,2}$")

    fun isAcceptableTempInput(input: String): Boolean = TEMP_PATTERN.matches(input)

    // 입력 완료 후 저장할 기온 값 (빈 값이나 "-"만 있으면 0)
    fun parseTemp(input: String): Int = input.toIntOrNull() ?: 0

    // DatePicker 선택값(UTC 자정 millis) -> "yyyy-MM-dd"
    fun millisToDateString(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()

    // "yyyy-MM-dd" -> DatePicker 초기값(UTC 자정 millis)
    fun dateStringToMillis(date: String): Long? = runCatching {
        LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }.getOrNull()

    // 귀국일이 출발일 이후(같은 날 포함)인지
    fun isValidDateRange(startDate: String, endDate: String): Boolean {
        val start = runCatching { LocalDate.parse(startDate) }.getOrNull() ?: return false
        val end = runCatching { LocalDate.parse(endDate) }.getOrNull() ?: return false
        return !end.isBefore(start)
    }
}
