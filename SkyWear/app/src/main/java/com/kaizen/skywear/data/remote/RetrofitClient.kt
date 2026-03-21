package com.kaizen.skywear.data.remote

import com.kaizen.skywear.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Logging Interceptor: 디버그 빌드에서 APi 요청/응답 로그 작성
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient : 타임아웃 및 로깅 설정
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS) // 연결 타임아웃
        .readTimeout(15, TimeUnit.SECONDS) // 읽기 타임아웃
        .writeTimeout(15, TimeUnit.SECONDS) // 쓰기 타임아웃
        .addInterceptor(loggingInterceptor) // 로그 인터셉터
        .build()

    // Retrofit 인스턴스
    val instance: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}