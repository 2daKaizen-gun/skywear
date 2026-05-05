package com.kaizen.skywear.di

import android.content.Context
import com.kaizen.skywear.data.local.ChecklistDao
import com.kaizen.skywear.data.local.SkyWearDatabase
import com.kaizen.skywear.data.local.SubscribedCityDao
import com.kaizen.skywear.data.local.TravelJournalDao
import com.kaizen.skywear.data.remote.RetrofitClient
import com.kaizen.skywear.data.remote.WeatherApiService
import com.kaizen.skywear.data.repository.ChecklistRepository
import com.kaizen.skywear.data.repository.JournalRepository
import com.kaizen.skywear.data.repository.SubscribeRepository
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
    @Provides @Singleton
    fun provideWeatherApiService(): WeatherApiService = RetrofitClient.instance

    @Provides @Singleton
    fun provideWeatherRepository(): WeatherRepository = WeatherRepository()

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SkyWearDatabase =
        SkyWearDatabase.getDatabase(context)

    @Provides @Singleton
    fun provideChecklistDao(db: SkyWearDatabase): ChecklistDao = db.checklistDao()

    @Provides @Singleton
    fun provideSubscribedCityDao(db: SkyWearDatabase): SubscribedCityDao = db.subscribedCityDao()

    @Provides @Singleton
    fun provideTravelJournalDao(db: SkyWearDatabase): TravelJournalDao = db.travelJournalDao()

    @Provides @Singleton
    fun provideChecklistRepository(dao: ChecklistDao): ChecklistRepository = ChecklistRepository(dao)

    @Provides @Singleton
    fun provideSubscribeRepository(dao: SubscribedCityDao): SubscribeRepository =
        SubscribeRepository(dao)

    @Provides @Singleton
    fun provideJournalRepository(dao: TravelJournalDao): JournalRepository = JournalRepository(dao)

    @Provides @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository =
        UserPreferencesRepository(context)
}