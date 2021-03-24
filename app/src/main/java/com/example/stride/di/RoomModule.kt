package com.example.stride.di

import android.content.Context
import com.example.stride.data.local.AppDatabase
import com.example.stride.data.local.dao.StepsRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.newInstance(context)

    @Provides
    fun provideStepsRecordDao(
        appDatabase: AppDatabase
    ): StepsRecordDao = appDatabase.stepRecordDao()

}