package com.zabackaryc.xkcdviewer.di

import android.util.Log
import com.zabackaryc.xkcdviewer.BuildConfig
import com.zabackaryc.xkcdviewer.network.ComicsApi
import com.zabackaryc.xkcdviewer.network.ExplainApi
import com.zabackaryc.xkcdviewer.network.listingConverter.ListingConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
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
    fun provideOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor { chain ->
            val request = chain.request()
            return@addInterceptor try {
                chain.proceed(request)
            } catch (e: Exception) {
                Log.d(
                    "xkcdviewer",
                    "Something went wrong when fetching responses from the server; returning 504 to client"
                )
                Log.e("xkcdviewer", e.toString())
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .message("Gateway Timeout").code(504)
                    .body("".toResponseBody(null))
                    .build()
            }
        }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            val logConfigInterceptor = Interceptor { chain ->
                val request = chain.request()
                // Create condition to exclude resource(s) from logs
                val logBody: Boolean = request.url.encodedPath != "/archive/"
                loggingInterceptor.setLevel(if (logBody) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
                chain.proceed(request)
            }
            client.addInterceptor(logConfigInterceptor)
            client.addInterceptor(loggingInterceptor)
        }
        return client.build()
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
