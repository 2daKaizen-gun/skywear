package com.kaizen.skywear.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// 모든 API 요청 및 응답을 가로채 공통 에러 처리

// 앱 전체에서 사용하는 커스텀 네트워크 에러
sealed class NetworkException(message: String) : IOException(message) {

    // 인터넷 연결 없음
    data object NoInternet : NetworkException(
        "인터넷 연결을 확인해주세요."
    )

    // 요청 타임아웃
    data object Timeout : NetworkException(
        "요청 시간 초과되었습니다. 다시 시도해주세요."
    )

    // API Key 오류(401)
    data object Unauthorized : NetworkException(
        "API 인증에 실패했습니다. API Key를 확인해주세요."
    )

    // 도시를 찾을 수 없음(404)
    data object CityNotFound : NetworkException(
        "도시를 찾을 수 없습니다. 도시명을 확인해주세요."
    )

    // API 호출 한도 초과(429)
    data object RateLimitExceeded : NetworkException(
        "API 호출 한도를 초과했습니다. 잠시 후 다시 시도해주세요."
    )

    // 서버 오류(5xx)
    data object ServerError : NetworkException(
        "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
    )

    // 알 수 없는 오류
    data class Unknown(val code: Int) : NetworkException(
        "알 수 없는 오류가 발생했습니다. (code: $code)"
    )
}