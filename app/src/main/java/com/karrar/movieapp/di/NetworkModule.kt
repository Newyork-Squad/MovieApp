package com.karrar.movieapp.di

import com.google.gson.Gson
import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.remote.AuthInterceptor
import com.karrar.movieapp.data.LanguageInterceptor
import com.karrar.movieapp.data.local.AppConfiguration
import com.karrar.movieapp.data.remote.service.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideMovieService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()

    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        languageInterceptor: LanguageInterceptor,): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(languageInterceptor)
            .build()
    }
    @Singleton
    @Provides
    fun provideLanguageInterceptor(
        appConfiguration: AppConfiguration
    ): LanguageInterceptor {
        return LanguageInterceptor {
            appConfiguration.getLanguage().first()
        }
    }


    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

}