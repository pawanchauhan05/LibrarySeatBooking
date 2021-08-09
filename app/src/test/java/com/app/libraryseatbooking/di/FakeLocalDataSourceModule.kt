package com.app.libraryseatbooking.di

import com.app.libraryseatbooking.data.source.local.FakeLocalDataSource
import com.app.libraryseatbooking.data.source.local.ILocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LocalDataSourceModule::class]
)
object FakeLocalDataSourceModule {

    @Singleton
    @Provides
    fun provideLocalDataSource(): ILocalDataSource {
        return FakeLocalDataSource()
    }
}