package com.obrien.core.di

import android.content.Context
import androidx.room.Room
import com.obrien.core.data.DataStoreManager
import com.obrien.core.data.HomeworkDao
import com.obrien.core.data.JournalDao
import com.obrien.core.data.JournalDatabase
import com.obrien.core.data.MIGRATION_1_2
import com.obrien.core.data.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideJournalDatabase(@ApplicationContext context: Context): JournalDatabase =
        Room.databaseBuilder(
            context,
            JournalDatabase::class.java,
            "journal_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideJournalDao(db: JournalDatabase): JournalDao = db.journalDao()

    @Provides
    fun provideHomeworkDao(db: JournalDatabase): HomeworkDao = db.homeworkDao()
}
