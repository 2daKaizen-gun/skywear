package com.kaizen.skywear.data

import com.kaizen.skywear.R
import com.kaizen.skywear.data.remote.NetworkException
import com.kaizen.skywear.data.remote.toErrorMessageRes
import com.kaizen.skywear.domain.getOutfitRecommendation
import com.kaizen.skywear.domain.mainOutfitRes
import org.junit.Assert.assertEquals
import org.junit.Test

// 에러 메시지 / 코디 문자열 리소스 매핑 단위 테스트

class ResourceMappingTest {

    @Test
    fun `네트워크 예외는 대응하는 문자열 리소스로 매핑된다`() {
        assertEquals(R.string.error_no_internet, NetworkException.NoInternet.toErrorMessageRes())
        assertEquals(R.string.error_timeout, NetworkException.Timeout.toErrorMessageRes())
        assertEquals(R.string.error_city_not_found, NetworkException.CityNotFound.toErrorMessageRes())
        assertEquals(R.string.error_unauthorized, NetworkException.Unauthorized.toErrorMessageRes())
        assertEquals(R.string.error_rate_limit, NetworkException.RateLimitExceeded.toErrorMessageRes())
        assertEquals(R.string.error_server, NetworkException.ServerError.toErrorMessageRes())
    }

    @Test
    fun `알 수 없는 예외나 null은 기본 에러 메시지로 매핑된다`() {
        assertEquals(R.string.error_unknown, NetworkException.Unknown(418).toErrorMessageRes())
        assertEquals(R.string.error_unknown, IllegalStateException().toErrorMessageRes())
        assertEquals(R.string.error_unknown, (null as Throwable?).toErrorMessageRes())
    }

    @Test
    fun `코디 단계별 현지화 문자열 리소스 매핑`() {
        assertEquals(R.string.outfit_stage1, getOutfitRecommendation(30.0).mainOutfitRes())
        assertEquals(R.string.outfit_stage5, getOutfitRecommendation(10.0).mainOutfitRes())
        assertEquals(R.string.outfit_stage8, getOutfitRecommendation(-10.0).mainOutfitRes())
    }
}
