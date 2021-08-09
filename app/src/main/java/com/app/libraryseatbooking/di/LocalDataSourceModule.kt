package com.app.libraryseatbooking.di

import android.content.Context
import androidx.room.Room
import com.app.libraryseatbooking.data.source.local.AppDatabase
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import com.app.libraryseatbooking.data.source.local.LocalDataSource
import com.app.libraryseatbooking.utilities.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            Config.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(appDatabase: AppDatabase, dispatcher: CoroutineDispatcher) : ILocalDataSource {
        return LocalDataSource(appDatabase.getQRCodeDataDao(), dispatcher)
    }

}