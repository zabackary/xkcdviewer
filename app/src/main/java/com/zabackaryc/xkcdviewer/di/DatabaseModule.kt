package com.zabackaryc.xkcdviewer.di

import android.content.Context
import androidx.room.Room
import com.zabackaryc.xkcdviewer.data.AppDatabase
import com.zabackaryc.xkcdviewer.data.ComicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    fun provideAppDao(appDatabase: AppDatabase): ComicDao {
        return appDatabase.getAppDao()
    }

}
