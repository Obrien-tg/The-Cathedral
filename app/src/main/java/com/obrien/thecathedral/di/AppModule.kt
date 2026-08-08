package com.obrien.thecathedral.di

import android.content.Context
import com.obrien.thecathedral.data.DataStoreManager
import com.obrien.thecathedral.data.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideScheduleRepository(dataStore: DataStoreManager): ScheduleRepository =
        ScheduleRepository(dataStore)
}
