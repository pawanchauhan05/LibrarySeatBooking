package com.app.libraryseatbooking.di

import com.app.libraryseatbooking.data.source.DataRepository
import com.app.libraryseatbooking.data.source.IDataRepository
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import com.app.libraryseatbooking.data.source.remote.IRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataRepositoryModule {

    @Singleton
    @Provides
    fun provideDataRepository(
        localDataSource: ILocalDataSource,
        remoteDataSource: IRemoteDataSource,
        dispatcher: CoroutineDispatcher
    ): IDataRepository {
        return DataRepository(
            localDataSource, remoteDataSource, dispatcher
        )
    }
}