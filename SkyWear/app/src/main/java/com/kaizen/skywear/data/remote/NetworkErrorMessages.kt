package com.kaizen.skywear.data.remote

import androidx.annotation.StringRes
import com.kaizen.skywear.R

// 네트워크 예외 -> 사용자에게 보여줄 현지화 문자열 리소스
@StringRes
fun Throwable?.toErrorMessageRes(): Int = when (this) {
    is NetworkException.NoInternet        -> R.string.error_no_internet
    is NetworkException.Timeout           -> R.string.error_timeout
    is NetworkException.CityNotFound      -> R.string.error_city_not_found
    is NetworkException.Unauthorized      -> R.string.error_unauthorized
    is NetworkException.RateLimitExceeded -> R.string.error_rate_limit
    is NetworkException.ServerError       -> R.string.error_server
    else                                  -> R.string.error_unknown
}
