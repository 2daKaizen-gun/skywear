package com.kaizen.skywear.di

import android.content.Context
import com.kaizen.skywear.data.local.ChecklistDao
import com.kaizen.skywear.data.local.SkyWearDatabase
import com.kaizen.skywear.data.remote.RetrofitClient
import com.kaizen.skywear.data.remote.WeatherApiService
import com.kaizen.skywear.data.repository.ChecklistRepository
import com.kaizen.skywear.data.repository.UserPreferencesRepository
import com.kaizen.skywear.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Hilt DI 모듈 - 앱 전역에 의존성 주입 설정
// SingletonComponent: 앱 생명주기와 동일함

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // WeatherApiService - Retrofit 싱글톤
    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService {
        return RetrofitClient.instance
    }

    // WeatherRepository
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService
    ): WeatherRepository {
        return WeatherRepository()
    }

    // Room Database
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SkyWearDatabase {
        return SkyWearDatabase.getDatabase(context)
    }

    // ChecklistDao
    @Provides
    @Singleton
    fun provideChecklistDao(
        database: SkyWearDatabase
    ): ChecklistDao {
        return database.checklistDao()
    }

    // ChecklistRepository
    @Provides
    @Singleton
    fun provideChecklistRepository(
        dao: ChecklistDao
    ): ChecklistRepository {
        return ChecklistRepository(dao)
    }

    // UserPreferencesRepository
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
}