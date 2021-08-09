package com.app.libraryseatbooking.di

import com.app.libraryseatbooking.data.source.remote.FakeRemoteDataSource
import com.app.libraryseatbooking.data.source.remote.IRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteDataSourceModule::class]
)
class FakeRemoteDataSourceModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(): IRemoteDataSource {
        return FakeRemoteDataSource()
    }
}