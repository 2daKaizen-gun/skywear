package com.kaizen.skywear.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Hilt DI 모듈 - 앱 전역에 의존성 주입 설정
// SingletonComponent: 앱 생명주기와 동일함

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


}