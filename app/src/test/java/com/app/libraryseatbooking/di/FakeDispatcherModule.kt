package com.app.libraryseatbooking.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatcherModule::class]
)
class FakeDispatcherModule {

    @Singleton
    @Provides
    @ExperimentalCoroutinesApi
    fun providesDispatcher() : CoroutineDispatcher = TestCoroutineDispatcher()
}