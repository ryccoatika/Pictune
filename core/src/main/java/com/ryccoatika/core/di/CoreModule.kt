package com.ryccoatika.core.di

import com.ryccoatika.core.BuildConfig
import com.ryccoatika.core.data.FavoriteRespository
import com.ryccoatika.core.data.HistoryRepository
import com.ryccoatika.core.data.UnsplashRepository
import com.ryccoatika.core.data.source.local.LocalDataSource
import com.ryccoatika.core.data.source.local.room.AppDatabase
import com.ryccoatika.core.data.source.remote.RemoteDataSource
import com.ryccoatika.core.data.source.remote.retrofit.UnsplashService
import com.ryccoatika.core.domain.repository.IFavoriteRepository
import com.ryccoatika.core.domain.repository.IHistoryRepository
import com.ryccoatika.core.domain.repository.IUnsplashRepository
import com.ryccoatika.core.domain.usecase.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        Retrofit.Builder().apply {
            addConverterFactory(GsonConverterFactory.create())
            baseUrl(BuildConfig.UNSPLASH_ENDPOIN)
            client(get())
        }.build().create(UnsplashService::class.java)
    }

    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.NONE))
            connectTimeout(120, TimeUnit.SECONDS)
            readTimeout(120, TimeUnit.SECONDS)
        }.build()
    }
}

val localModule = module {
    single {
        AppDatabase.getInstance(get()).getFavoriteDao()
    }
    single { 
        AppDatabase.getInstance(get()).getHistoryDao()
    }
}

val repositoryModule = module {
    single { RemoteDataSource(get()) }
    single { LocalDataSource(get(), get()) }
    single<IHistoryRepository> { HistoryRepository(get()) }
    single<IUnsplashRepository> { UnsplashRepository(get()) }
    single<IFavoriteRepository> { FavoriteRespository(get()) }
}

val useCaseModule = module {
    single<HistoryUseCase> { HistoryInteractor(get()) }
    single<UnsplashUseCase> { UnsplashInteractor(get()) }
    single<FavoriteUseCase> { FavoriteInteractor(get()) }
}