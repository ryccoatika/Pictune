package com.ryccoatika.pictune.db

import com.ryccoatika.pictune.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

object NetworkAdapter {
    fun providesHttpAdapter(url: URL, modeDownload: Boolean = false): Retrofit {
        return Retrofit.Builder().also {
            it.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            it.addConverterFactory(GsonConverterFactory.create())
            it.baseUrl(url)
            it.client(
                if (modeDownload)
                    providesHttpClientForDownload()
                else
                    providesHttpClient()
            )
        }.build()
    }

    private fun providesHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().also {
            it.addInterceptor(providesHttpLoggingInterceptor())
            it.retryOnConnectionFailure(true)
        }.build()
    }

    private fun providesHttpClientForDownload(): OkHttpClient {
        return OkHttpClient.Builder().also {
            it.addInterceptor(providesHttpLoggingInterceptor())
            it.readTimeout(2, TimeUnit.MINUTES)

        }.build()
    }

    private fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().also {
            it.level = when (BuildConfig.DEBUG) {
                true -> HttpLoggingInterceptor.Level.BODY
                false -> HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}