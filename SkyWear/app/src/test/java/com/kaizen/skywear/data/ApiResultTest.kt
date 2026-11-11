package com.kaizen.skywear.data

import com.kaizen.skywear.data.remote.NetworkException
import com.kaizen.skywear.data.repository.apiResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

// Repository API 결과 래핑 단위 테스트

class ApiResultTest {

    @Test
    fun `성공한 호출은 Result success로 감싼다`() {
        assertEquals(Result.success(42), apiResult { 42 })
    }

    @Test
    fun `네트워크 예외는 Result failure로 바꾼다`() {
        val result = apiResult<Int> { throw NetworkException.Timeout }
        assertTrue(result.exceptionOrNull() is NetworkException.Timeout)
    }

    @Test(expected = CancellationException::class)
    fun `코루틴 취소는 실패로 삼키지 않고 다시 던진다`() {
        apiResult<Int> { throw CancellationException("cancelled") }
    }
}
