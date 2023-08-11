package com.zabackaryc.xkcdviewer.di

import com.zabackaryc.xkcdviewer.BuildConfig
import com.zabackaryc.xkcdviewer.network.ComicsApi
import com.zabackaryc.xkcdviewer.network.ExplainApi
import com.zabackaryc.xkcdviewer.network.listingConverter.ListingConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    @Named("Comics")
    fun provideRetrofitComics(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://xkcd.com/")
        .addConverterFactory(
            ListingConverterFactory.create(
                "https://xkcd.com/",
                listOf("/archive/")
            )
        )
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideComicsApiService(@Named("Comics") retrofit: Retrofit): ComicsApi =
        retrofit.create(ComicsApi::class.java)

    @Singleton
    @Provides
    @Named("Explain")
    fun provideRetrofitExplain(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://www.explainxkcd.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideExplainApiService(@Named("Explain") retrofit: Retrofit): ExplainApi =
        retrofit.create(ExplainApi::class.java)

}
