package com.obrien.thecathedral.di

import android.content.Context
import androidx.room.Room
import com.obrien.thecathedral.data.DataStoreManager
import com.obrien.thecathedral.data.JournalDao
import com.obrien.thecathedral.data.JournalDatabase
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
    fun provideJournalDatabase(@ApplicationContext context: Context): JournalDatabase =
        Room.databaseBuilder(
            context,
            JournalDatabase::class.java,
            "journal_database"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideJournalDao(db: JournalDatabase): JournalDao = db.journalDao()
}
