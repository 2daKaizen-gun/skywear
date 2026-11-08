package com.kaizen.skywear.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// 모든 API 요청 및 응답을 가로채 공통 에러 처리
class NetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)

            // 성공(2xx)은 그대로 반환
            if (response.isSuccessful) return response

            // 에러 응답은 throw 전에 body를 닫아 커넥션 누수 방지
            val code = response.code
            response.close()

            // HTTP 상태코드별 에러 처리
            throw when (code) {
                401 -> NetworkException.Unauthorized // API Key 오류
                404 -> NetworkException.CityNotFound // 도시를 찾을 수 없음
                429 -> NetworkException.RateLimitExceeded // API 호출 한도 초과
                in 500..599 -> NetworkException.ServerError // 서버 오류
                else -> NetworkException.Unknown(code)
            }

        } catch (e: SocketTimeoutException) {
            throw NetworkException.Timeout // 타임아웃
        } catch (e: UnknownHostException) {
            throw NetworkException.NoInternet // 인터넷 연결 없음
        } catch (e: ConnectException) {
            throw NetworkException.NoInternet // 연결 실패 (네트워크 없음)
        } catch (e: NetworkException) {
            throw e // 이미 처리된 에러는 그대로 전달
        } catch (e: IOException) {
            throw NetworkException.Unknown(-1) // 그 외 IO 에러
        }
    }
}

// 앱 전체에서 사용하는 커스텀 네트워크 에러
// message는 로그용 — 화면 표시 문구는 toErrorMessageRes()로 현지화된 리소스를 사용
sealed class NetworkException(message: String) : IOException(message) {

    // 인터넷 연결 없음
    data object NoInternet : NetworkException(
        "No internet connection"
    )

    // 요청 타임아웃
    data object Timeout : NetworkException(
        "Request timed out"
    )

    // API Key 오류(401)
    data object Unauthorized : NetworkException(
        "Unauthorized - check API key"
    )

    // 도시를 찾을 수 없음(404)
    data object CityNotFound : NetworkException(
        "City not found"
    )

    // API 호출 한도 초과(429)
    data object RateLimitExceeded : NetworkException(
        "Rate limit exceeded"
    )

    // 서버 오류(5xx)
    data object ServerError : NetworkException(
        "Server error"
    )

    // 알 수 없는 오류
    data class Unknown(val code: Int) : NetworkException(
        "Unknown error (code: $code)"
    )
}